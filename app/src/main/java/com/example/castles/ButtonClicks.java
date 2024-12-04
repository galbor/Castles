package com.example.castles;


import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import java.util.LinkedList;
import java.util.List;


/**
 * related to the game itself
 */
public class ButtonClicks {

    private static class RoomNumber {
        public ROOM room;
        public int number;

        public RoomNumber(ROOM room, int number) {
            this.room = room;
            this.number = number;
        }
    }

    private static List<Game.COLOR> castle_order = new LinkedList<>(); //need way to show castle_order

    public static Game game = null;
    public static int castle_pos;
    private static ROOM cur_room;
    private static int cur_room_number;

    private static ROOM cur_room_for_garden;

    private static boolean game_over = false;

    private static UpdateTimeThread t;


    private static class UpdateTimeThread extends Thread{

        private Activity activity;
        private boolean running = true;

        public UpdateTimeThread(Activity activity){
            this.activity = activity;
        }
        public void run(){
            while (running){
                OnlineRoom.UpdateCastle(castle_pos, game.GetCastle(castle_pos), activity);
                try {
                    Thread.sleep(OnlineRoom.Is_Usable_Castle.millis_between_updates);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void kill(){running = false;}
    }



    /**
     * start game based on the chosen castle_order
     */
    public static void StartGame(View view) {
        if (game == null) {
            Game.COLOR[] castles = new Game.COLOR[castle_order.size()];
            castle_order.toArray(castles);
            game = new Game(castles);
        }
        castle_pos = -1;
        cur_room_number = 0;
        cur_room = ROOM.yellow;
    }

    /**
     * updates the views to present the castle order
     *
     * @param icons array of IDs of _views_ that will _contain_ the icons
     */
    public static void UpdateIcons(Activity activity, int[] icons) {
        ImageView v;
        int i = 0;
        for (Game.COLOR c : castle_order) {
            v = activity.findViewById(icons[i++]);
            v.setImageResource(c.getDrawableId());
            v.setVisibility(View.VISIBLE);
        }
        if (castle_order.size() < Game.COLOR.values().length)
            activity.findViewById(icons[i]).setVisibility(View.INVISIBLE); //default color
    }

    public static void DisplayPassword(TextView textView, boolean newroom){
        if (newroom)
            OnlineRoom.CreateOnlineRoom(game, textView);
        else
            textView.setText(OnlineRoom.GetPassword());
    }

    /**
     * on click of the buttons that choose the castles
     */
    public static void OnColorClick(@NonNull Button b) {
        Game.COLOR color;
        switch (b.getId()) {
            case R.id.button_yellow:
                color = Game.COLOR.yellow;
                break;
            case R.id.button_red:
                color = Game.COLOR.red;
                break;
            case R.id.button_white:
                color = Game.COLOR.white;
                break;
            case R.id.button_green:
                color = Game.COLOR.green;
                break;
            case R.id.button_brown:
                color = Game.COLOR.brown;
                break;
            case R.id.button_lilac:
                color = Game.COLOR.lilac;
                break;
            case R.id.button_blue:
                color = Game.COLOR.blue;
                break;
            default:
                throw new IllegalMonitorStateException("Wrong button lead to onColorClick()");
        }
        if (castle_order.contains(color))
            castle_order.remove(color);
        else
            castle_order.add(color);

        UpdateIcons((Activity) b.getContext(), new int[]{R.id.castle1, R.id.castle2, R.id.castle3,
                R.id.castle4, R.id.castle5, R.id.castle6, R.id.castle7});
    }

    public static void OnNextPageClick(Button b) {
        final int MIN_CASTLES = 3;

        if (castle_order.size() >= MIN_CASTLES) {
            game = null;
            Navigation.findNavController(b).navigate(R.id.action_FirstFragment_to_SecondFragment);
        } else {
            b.setBackgroundColor(((Activity) b.getContext()).getColor(R.color.red));
            b.setText(R.string.too_little_castles_error); //find way to show error message
        }
    }

    public static void OnJoinRoomClick(Button b){
        EditText editText = ((Activity)b.getContext()).findViewById(R.id.enter_online_room_ID);
        String password = editText.getText().toString();
        OnlineRoom.GetGame(password, gameVar -> {
            if (gameVar == null){
                //error message
                editText.setText("");
                editText.setHint(R.string.wrong_password);
            }
            else{
                game = gameVar;
                //fragment 2 needs castle_order
                castle_order.clear();
                for (int i = 0; i < game.GetCastleAmt(); i++)
                    castle_order.add(game.GetColor(i));

                Navigation.findNavController(b).navigate(R.id.action_onlineFragment_to_secondFragment);
            }
        });
    }


    public static void OnCreateRoomClick(Button b){
        Navigation.findNavController(b).navigate(R.id.action_onlineFragment_to_firstFragment);
    }


    /**
     * starts the stage where you tell the game how many rooms in which castle.
     */
    public static void StartCastleRoomCount(android.widget.ImageButton b) {
        //check which castle clicks this and turn everything visible
        Activity activity = (Activity) b.getContext();

        int prev_castle_pos = castle_pos;
        Castle prev_castle = castle_pos >= 0 ? game.GetCastle(castle_pos) : null;
        int new_castle_pos;

        switch (b.getId()) {
            case R.id.castle_button2:
                new_castle_pos = 1;
                break;
            case R.id.castle_button3:
                new_castle_pos = 2;
                break;
            case R.id.castle_button4:
                new_castle_pos = 3;
                break;
            case R.id.castle_button5:
                new_castle_pos = 4;
                break;
            case R.id.castle_button6:
                new_castle_pos = 5;
                break;
            case R.id.castle_button7:
                new_castle_pos = 6;
                break;
            default:
                new_castle_pos = 0;
                break;
        }
        cur_room_number = 0;

        OnlineRoom.GetCastle(new_castle_pos, prev_castle_pos, prev_castle, activity , castle -> {
            int views_to_reveal[] = new int[]{R.id.number, R.id.plusButton, R.id.minusButton,
                    R.id.next_room, R.id.previous_room, R.id.done_count, R.id.room, R.id.roomNumber, R.id.pound};

            castle_pos = new_castle_pos;

            SetCastle(castle_pos, castle);

            activity.runOnUiThread(()->{
            if (castle.readylevel == READYLEVEL.donescoring) {
                SetVisibility(activity, views_to_reveal, View.GONE);
                DisplayCastleScore(activity, castle_pos);
            } else {
                cur_room = castle.last_changed;
                SetVisibility(activity, views_to_reveal, View.VISIBLE);
                DisplayRoomIcon(activity);
                SetRoomAmt(activity);
                if (castle.readylevel == READYLEVEL.unready) {
                    SetVisibility(activity, new int[]{R.id.pound, R.id.roomNumber}, View.GONE);
                } else {
                    DoneCountReveal(activity);
                }
            }

            ((ImageView) activity.findViewById(R.id.current_castle)).
                    setImageResource(castle_order.get(castle_pos).getDrawableId());
            });

            if (t != null) t.kill();
            if (!game.AllCastlesDone()) {
                t = new UpdateTimeThread(activity);
                t.start();
            }
        });
    }

    public static void OnCastleImageClick(android.widget.ImageButton b) {
        Activity activity = (Activity) b.getContext();
        if (activity.findViewById(R.id.number).getVisibility() == View.VISIBLE) {
            game.GetCastle(castle_pos).last_changed = cur_room;
            SaveRoomAmt(activity);
        }
        StartCastleRoomCount(b);
        DisplayScore(activity);
    }

    /**
     * adds or subtracts from count when clicked
     *
     * @param b could be either +plus or -minus button
     */
    public static void OnPlusClick(android.widget.Button b) {
        Activity activity = (Activity) b.getContext();
        EditText numberText = activity.findViewById(R.id.number);
        int value = Integer.parseInt(numberText.getText().toString());
        boolean add = (b.getId() == R.id.plusButton);
        if (cur_room == ROOM.green && game.IsDoneCounting(castle_pos)) {
            DisplayNextRoomForGarden(activity, add);
        }
        else
            numberText.setText("" + (add ? Math.min(value + 1, 32) : Math.max(value - 1, 0)));
    }

    public static void OnNextRoomClick(android.widget.Button b) {
        Activity activity = (Activity) b.getContext();
        boolean forward = (b.getId() == R.id.next_room);
        SaveRoomAmt(activity);
        if (!game.IsDoneCounting(castle_pos)) {
            cur_room = NextSkip(cur_room, new ROOM[]{ROOM.throne}, forward);
        } else {
            RoomNumber roomNumber = NextRoomNumber(castle_pos, cur_room, cur_room_number, forward);
            cur_room = roomNumber.room;
            cur_room_number = roomNumber.number;
            DisplayRoomNumber(activity);
        }
        SetRoomAmt(activity);
        game.GetCastle(castle_pos).last_changed = cur_room;
        DisplayRoomIcon(activity);
    }

    public static void OnDoneClick(android.widget.Button b) {
        Activity activity = (Activity) b.getContext();
        SaveRoomAmt(activity);
        if (!game.IsDoneCounting(castle_pos)) {
            game.DoneCount(castle_pos);
            DoneCountReveal(activity);
        } else {
            game.DoneScoring(castle_pos);
            SetVisibility(activity, new int[]{R.id.number, R.id.plusButton, R.id.minusButton,
                    R.id.next_room, R.id.previous_room, R.id.done_count, R.id.room,
                    R.id.roomNumber, R.id.pound, R.id.room_for_garden}, View.GONE);
            TurnOnCheckMark(castle_pos, R.drawable.checkmark, true, activity);
            DisplayCastleScore(activity, castle_pos);
            DisplayScore(activity);
            if (game.AllCastlesDone())
            {
                int[] winners = game.FindWinners();
                DisplayWinners(activity, winners);
            }
        }
        OnlineRoom.UpdateCastle(castle_pos, game.GetCastle(castle_pos), activity);
    }

    /**
     * if castle is done scoring, show a checkmark. else if it's occupied, show an occupied sign
     */
    public static void ShowCastleUsability(int castle_pos, boolean done_scoring, boolean occupied, Activity activity){
        int drawable = 0;
        if (done_scoring){
            drawable = R.drawable.checkmark;
        } else if (occupied && castle_pos != ButtonClicks.castle_pos){
            drawable =  R.drawable.occupied;
        }
        TurnOnCheckMark(castle_pos, drawable, done_scoring || occupied, activity);
    }

    /**
     * @param pos the specific castle to set
     * @param castle the new castle to put in the place
     */
    public static void SetCastle(int pos, Castle castle){
        game.SetCastle(pos, castle);
    }


    private static void DoneCountReveal(Activity activity) {
        cur_room = FirstRoom(game.GetCastle(castle_pos));
        cur_room_number = 0;
        SetVisibility(activity, new int[]{R.id.pound, R.id.roomNumber}, View.VISIBLE);
        DisplayRoomIcon(activity);
        DisplayRoomNumber(activity);
        SetRoomAmt(activity);
    }

    /**
     * sets to display the number of rooms of a certain type
     * can display room points too, if counting is done
     *
     * @param activity the Activity that contains a view called "number" that we want to display on
     */
    private static void SetRoomAmt(Activity activity) {
        TextView numberText = activity.findViewById(R.id.number);
        SwitchNumberForGarden(activity);
        if (!game.IsDoneCounting(castle_pos)) {
            numberText.setText("" + game.GetRoomAmt(castle_pos, cur_room));
        } else {
            numberText.setText("" + game.GetRoomPoints(castle_pos, cur_room, cur_room_number));
        }
    }

    /**
     * saves the number of rooms of a certain type
     * can save room points too, if counting is done
     *
     * @param activity the Activity that contains a view called "number" that we want to extract
     */
    private static void SaveRoomAmt(Activity activity) {
        TextView numberText = activity.findViewById(R.id.number);
        int num = Integer.parseInt(numberText.getText().toString());
        if (!game.IsDoneCounting(castle_pos))
            game.SetRoomAmt(castle_pos, cur_room, num);
        else {
            if (cur_room == ROOM.green)
                game.SetGardenRoomPoints(castle_pos, cur_room_number, cur_room_for_garden);
            else
                game.SetRoomPoints(castle_pos, cur_room, cur_room_number, num);
        }
    }


    /**
     * @return first room (type) the castle has
     */
    private static ROOM FirstRoom(Castle castle) {
        ROOM res = ROOM.values()[0];
        while (castle.GetRoomAmount(res) == 0) {
            res = NextSkip(res, new ROOM[]{ROOM.blue, ROOM.fountain}, true);
        }
        return res;
    }

    private static void DisplayRoomIcon(Activity activity, ROOM room) {
        ((TextView) activity.findViewById(R.id.roomName)).setText(room.getNameId());
        ((ImageView) activity.findViewById(R.id.room)).setImageResource(room.getPicID());
    }

    private static void DisplayRoomIcon(Activity activity) {
        DisplayRoomIcon(activity, cur_room);
    }

    private static void DisplayRoomNumber(Activity activity) {
        ((TextView) activity.findViewById(R.id.roomNumber)).setText("" + (cur_room_number + 1));
    }

    private static void DisplayCastleScore(Activity activity, int castle_pos) {
        if (!game_over)
            ((TextView) activity.findViewById(R.id.roomName)).setText(activity.getText(R.string.score) + ": " + game.GetCastle(castle_pos).PointsSum());
    }

    private static void DisplayWinners(Activity activity, int[] winners){
        TextView view = activity.findViewById(R.id.roomName);
        StringBuilder res = new StringBuilder("");
        res.append(activity.getText(R.string.winner));
        res.append(": ");
        for (int i : winners) {
            res.append(activity.getText(R.string.player));
            res.append(" ");
            res.append(activity.getText(R.string.pound));
            res.append(i);
            res.append("\n");
        }
        view.setText(res.toString());
        view.setTextSize(32);
        game_over = true;
    }

    private static ROOM NextRoom(ROOM room, int distance) {
        return ROOM.values()[Math.floorMod(room.ordinal() + distance, ROOM.values().length)];
    }

    private static boolean ObjectInArr(Object obj, Object[] arr) {
        for (Object o : arr) {
            if (o == obj) return true;
        }
        return false;
    }

    private static ROOM NextSkip(ROOM room, ROOM[] to_skip, int distance) {
        if (to_skip.length >= ROOM.values().length) return null;
        ROOM res = NextRoom(room, distance);
        while (ObjectInArr(res, to_skip)) {
            res = NextRoom(res, distance);
        }
        return res;
    }

    private static ROOM NextSkip(ROOM room, ROOM[] to_skip, boolean forward) {
        return forward ? NextSkip(room, to_skip, 1) : NextSkip(room, to_skip, -1);
    }

    private static RoomNumber NextRoomNumber(int castle_pos, ROOM room, int number, boolean forward) {
        ROOM res_room = room;
        int res_num = number;
        int distance = (forward ? 1 : -1);

        res_num += distance;
        if (res_num < 0 || res_num >= game.GetRoomAmt(castle_pos, res_room)) {
            do {
                res_room = NextRoom(res_room, distance);
            }
            while (game.GetRoomAmt(castle_pos, res_room) == 0 || res_room == ROOM.blue || res_room == ROOM.fountain);
            if (forward) res_num = 0;
            else res_num = game.GetRoomAmt(castle_pos, res_room) - 1;
        }
        return new RoomNumber(res_room, res_num);
    }

    /**
     * displays (or hides) a checkmark (or any other icon) on the selected castle
     * @param drawable drawable id to display in checkmark position
     * @param visible if to turn on or hide
     */
    private static void TurnOnCheckMark(int castle_pos, int drawable, boolean visible, Activity activity) {
        int[] checkmarks = {R.id.checkmark1, R.id.checkmark2, R.id.checkmark3, R.id.checkmark4, R.id.checkmark5, R.id.checkmark6, R.id.checkmark7};
        ImageView checkmarkview = activity.findViewById(checkmarks[castle_pos]);
        checkmarkview.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        checkmarkview.setImageResource(drawable);
    }

    private static void SetVisibility(Activity activity, int[] view_ids, int visibility) {
        for (int id : view_ids) {
            activity.findViewById(id).setVisibility(visibility);
        }
    }

    public static void DisplayScore(Activity activity) {
        TextView score_text = activity.findViewById(R.id.scores);
        StringBuilder res = new StringBuilder("");
        for (int i = 0; i < game.GetCastleAmt(); ++i) {
            res.append(activity.getString(R.string.castle));
            res.append(" ");
            res.append(activity.getString(game.GetColor(i).getStringId()));
            res.append(": ");
            if (game.IsDoneScoring(i))
                res.append(game.GetCastle(i).PointsSum());
            res.append("\n");
        }

        score_text.setText(res.toString());

        if (game.AllCastlesDone())
            DisplayWinners(activity, game.FindWinners());
    }

    private static void SwitchNumberForGarden(Activity activity) {
        View number = activity.findViewById(R.id.number);
        ImageView room_for_garden = activity.findViewById(R.id.room_for_garden);
        if (cur_room == ROOM.green && game.IsDoneCounting(castle_pos)) {
            cur_room_for_garden = game.GetGreenLastChanged(castle_pos, cur_room_number);
            number.setVisibility(View.INVISIBLE);
            room_for_garden.setVisibility(View.VISIBLE);
            room_for_garden.setImageResource(cur_room_for_garden.getPicID());
        } else {
            number.setVisibility(View.VISIBLE);
            room_for_garden.setVisibility(View.INVISIBLE);
        }
    }

    private static void DisplayNextRoomForGarden(Activity activity, boolean forward) {
        ImageView room_for_garden = activity.findViewById(R.id.room_for_garden);
        cur_room_for_garden = NextSkip(cur_room_for_garden, new ROOM[]{ROOM.green, ROOM.card, ROOM.attendant, ROOM.foyer, ROOM.fountain, ROOM.tower}, forward);
        room_for_garden.setImageResource(cur_room_for_garden.getPicID());
    }
}
