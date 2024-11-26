package com.example.castles;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * join, create, etc with online rooms
 */
public class OnlineRoom {

    private static final String COLLECTION_NAME = "CastlesGame";
    private static final int PASSWORD_LENGTH = 4;

    private static final String GAME_WORD = "Game";
    private static final String CASTLE_WORD = "Castle";
    private static final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    private static final CollectionReference db = fireStore.collection(COLLECTION_NAME);

    private static String password = null;

    /**
     * a castle and a boolean denoting whether one is allowed to access it
     */
    public static class Is_Usable_Castle {

        public static final int millis_between_updates = 3000;
        public static final int seconds_until_usable = 10;
        public Map<String, Object> castle;
        public Number last_used;

        public Is_Usable_Castle(Castle castle){
            UpdateTime();
            SetCastle(castle);
        }

        /**
         * factory method
         * @param snapshot that was uploaded using CreateOnlineRoom
         * @param castle_pos the number of the castle to pull
         */
        public static Is_Usable_Castle FromSnapshot(DocumentSnapshot snapshot, int castle_pos){
            Map<String, Object> map = (Map<String, Object>) snapshot.get(CastleNameDB(castle_pos));
            if (map == null) return null;
            return new Is_Usable_Castle(map);
        }

        private Is_Usable_Castle(Map<String, Object> map){
            this.last_used = (Number)map.get("last_used");
            this.castle = (Map<String, Object>)map.get("castle");
        }

        public void SetCastle(Castle castle){
            this.castle = castle.ToMap();
        }

        public Castle GetCastle(){
            return Castle.FromMap(this.castle);
        }

        /**
         * sets last_used to now
         */
        public void UpdateTime(){
            this.last_used = Now();
        }

        private Number Now(){
            return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        }

        public boolean IsUsable(){
            return Now().longValue() - last_used.longValue() >= seconds_until_usable;
        }

        /**
         * sets the castle to be usable
         * @return this
         */
        public Is_Usable_Castle MakeUsable(){
            this.last_used = 0;
            return this;
        }

        /**
         * if donescoring make it usable, otherwise update time
         */
        public void UpdateUsability(){
            if (GetCastle().readylevel == READYLEVEL.donescoring)
                MakeUsable();
            else
                UpdateTime();
        }
    }

    public static String GetPassword() {return password;}

    /**
     * creates an online room, displays the password in textview
     *
     * @param game to put in the online room
     * @param textView place to display the password
     *
     */
    public static void CreateOnlineRoom(Game game, TextView textView){
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(GetRandomPasswordChar(rand));
        }

        password = sb.toString();


        Map<String, Object> document = new HashMap<>();
        document.put(GAME_WORD, game.CastleOrderOrdinals());
        for (int i = 0; i<game.GetCastleAmt(); i++){
            document.put(CastleNameDB(i), new Is_Usable_Castle(game.GetCastle(i)).MakeUsable());
        }


        db.document(sb.toString()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Log.d("Failed with: ", task.getException().toString());
                return;
            }
            if (task.getResult().exists()){
                Log.d("Generated already used password: ", password); //not recursive
                CreateOnlineRoom(game, textView); //not actually recursive, because of time delay. I hope
                return;
            }


            db.document(password).set(document);
            textView.setText(password);
        });
    }

    /**
     * get the game from the database
     * @param pass the password
     * @param action what to do when gets the game. the parameter will be null if the password is wrong
     */
    public static void GetGame(String pass, Consumer<Game> action){

        db.document(pass).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Log.d("Failed with: ", task.getException().toString());
                return;
            }
            DocumentSnapshot document = task.getResult();
            if (document.exists()){
                action.accept(new Game(Game.CastleOrderFromOrdinals((List<Number>)document.get(GAME_WORD))));
                password = pass;
                return;
            }
            //if the password is wrong
            action.accept(null);
        });
    }


    /**
     * get the castle from the database (if it's available and free). If it is, locks the castle.
     * if locked a castle previously, unlock it
     * @param pos the position of the castle
     * @param prevPos previously viewed castle (can be -1 if there is none)
     * @param action what to do if the castle is available and free
     */
    public static void GetCastle(int pos, int prevPos, Castle prev_castle, Activity activity, Consumer<Castle> action){
        DocumentReference docRef = db.document(password);
        fireStore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            Is_Usable_Castle iuCastle = Is_Usable_Castle.FromSnapshot(snapshot, pos);

            assert iuCastle != null;

            ShowCastlesUsability(snapshot, activity, prevPos, Is_Usable_Castle.FromSnapshot(snapshot, prevPos));

            if (!iuCastle.IsUsable()) return null;

            //updates the previous castle
            if (prevPos > -1) {
                Is_Usable_Castle prevIuCastle = Is_Usable_Castle.FromSnapshot(snapshot, prevPos);
                prevIuCastle.MakeUsable();
                prevIuCastle.SetCastle(prev_castle);
                transaction.update(docRef, CastleNameDB(prevPos), prevIuCastle);
            }

            Castle castle = iuCastle.GetCastle();

            iuCastle.UpdateUsability();

            transaction.update(docRef, CastleNameDB(pos), iuCastle);
            action.accept(castle);

            return null;
        }).addOnSuccessListener(aVoid -> Log.d("success", "Transaction success GetCastle!"))
                .addOnFailureListener(e -> Log.w("fail", "Transaction failure GetCastle.", e));
    }

    public static void UpdateCastle(int pos, Castle castle, Activity activity){
        DocumentReference docRef = db.document(password);
        fireStore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            Is_Usable_Castle iuCastle = Is_Usable_Castle.FromSnapshot(snapshot, pos);

            assert iuCastle != null;

            iuCastle.SetCastle(castle);
            iuCastle.UpdateUsability();

            transaction.update(docRef, CastleNameDB(pos), iuCastle);

            ShowCastlesUsability(snapshot, activity, pos, iuCastle);

            return null;
        }).addOnSuccessListener(aVoid -> Log.d("success", "Transaction success UpdateCastle!"))
                .addOnFailureListener(e -> Log.w("fail", "Transaction failure UpdateCastle.", e));;
    }


    /**
     * @param rand
     * @return random digit or upper/lower letter
     */
    private static char GetRandomPasswordChar(Random rand){
        int n = rand.nextInt(10+26+26);
        if (n<10) return (char) ('0' + n);
        n -= 10;
        if (n<26) return (char) ('a'+n);
        n-=26;
        return (char) ('A'+n);
    }

    /**
     *
     * @param pos
     * @return the name of the castle's document in the db
     */
    private static String CastleNameDB(int pos){
        return CASTLE_WORD + pos;
    }

    /**
     * updates all castles' usability and done-ness (with the checkmark or occupied icons
     * @param prev_castle_pos the pos of the previous castle. it is always considered usable
     * @param prev_castle the previous castle. the castle in the snapshow isn't updated
     */
    public static void ShowCastlesUsability(DocumentSnapshot snapshot, Activity activity, int prev_castle_pos, Is_Usable_Castle prev_castle){
        Game game = new Game(Game.CastleOrderFromOrdinals((List<Number>)snapshot.get(GAME_WORD)));
        activity.runOnUiThread(()->{
        for (int i = 0; i<game.GetCastleAmt(); i++){
            Is_Usable_Castle iuCastle = i == prev_castle_pos ? prev_castle : Is_Usable_Castle.FromSnapshot(snapshot, i);
            ButtonClicks.ShowCastleUsability(i, iuCastle.GetCastle().readylevel == READYLEVEL.donescoring, !iuCastle.IsUsable() && i != prev_castle_pos, activity);
        }});
    }
}
