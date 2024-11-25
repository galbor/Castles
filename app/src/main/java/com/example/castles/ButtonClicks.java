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

    /**
     * start game based on the chosen castle_order
     */
    public static void startGame(View view) {
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
    public static void updateIcons(Activity activity, int[] icons) {
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

    public static void displayPassword(TextView textView, boolean newroom){
        if (newroom)
            OnlineRoom.CreateOnlineRoom(game, textView);
        else
            textView.setText(OnlineRoom.GetPassword());
    }

    /**
     * on click of the buttons that choose the castles
     */
    public static void onColorClick(@NonNull Button b) {
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

        updateIcons((Activity) b.getContext(), new int[]{R.id.castle1, R.id.castle2, R.id.castle3,
                R.id.castle4, R.id.castle5, R.id.castle6, R.id.castle7});
    }

    public static void onNextPageClick(Button b) {
        final int MIN_CASTLES = 3;

        if (castle_order.size() >= MIN_CASTLES) {
            game = null;
            Navigation.findNavController(b).navigate(R.id.action_FirstFragment_to_SecondFragment);
        } else {
            b.setBackgroundColor(((Activity) b.getContext()).getColor(R.color.red));
            b.setText(R.string.too_little_castles_error); //find way to show error message
        }
    }

    public static void onJoinRoomClick(Button b){
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
                for (int i = 0; i < game.get_castle_amt(); i++)
                    castle_order.add(game.get_color(i));

                Navigation.findNavController(b).navigate(R.id.action_onlineFragment_to_secondFragment);
            }
        });
    }


    public static void onCreateRoomClick(Button b){
        Navigation.findNavController(b).navigate(R.id.action_onlineFragment_to_firstFragment);
    }


    /**
     * starts the stage where you tell the game how many rooms in which castle.
     */
    public static void startCastleRoomCount(android.widget.ImageButton b) {
        //check which castle clicks this and turn everything visible
        Activity activity = (Activity) b.getContext();

        int prev_castle_pos = castle_pos;
        Castle prev_castle = castle_pos >= 0 ? game.get_castle(castle_pos) : null;
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

            game.set_castle(castle_pos, castle);

            activity.runOnUiThread(()->{
            if (castle.done_counting == READYLEVEL.done) {
                set_visibility(activity, views_to_reveal, View.GONE);
                set_castle_score(activity, castle_pos);
            } else {
                cur_room = castle.last_changed;
                set_visibility(activity, views_to_reveal, View.VISIBLE);
                set_room_icon(activity);
                set_room_amt(activity);
                if (castle.done_counting == READYLEVEL.unready) {
                    set_visibility(activity, new int[]{R.id.pound, R.id.roomNumber}, View.GONE);
                } else {
                    doneCountReveal(activity);
                }
            }

            ((ImageView) activity.findViewById(R.id.current_castle)).
                    setImageResource(castle_order.get(castle_pos).getDrawableId());
            });
        });
    }

    public static void onCastleImageClick(android.widget.ImageButton b) {
        Activity activity = (Activity) b.getContext();
        if (activity.findViewById(R.id.number).getVisibility() == View.VISIBLE) {
            game.get_castle(castle_pos).last_changed = cur_room;
            save_room_amt(activity);
        }
        startCastleRoomCount(b);
    }

    /**
     * adds or subtracts from count when clicked
     *
     * @param b could be either +plus or -minus button
     */
    public static void onPlusClick(android.widget.Button b) {
        Activity activity = (Activity) b.getContext();
        EditText numberText = activity.findViewById(R.id.number);
        int value = Integer.parseInt(numberText.getText().toString());
        boolean add = (b.getId() == R.id.plusButton);
        if (cur_room == ROOM.green && game.is_done_counting(castle_pos)) {
            display_next_room_for_garden(activity, add);
        }
        else
            numberText.setText("" + (add ? Math.min(value + 1, 32) : Math.max(value - 1, 0)));
    }

    public static void onNextRoomClick(android.widget.Button b) {
        Activity activity = (Activity) b.getContext();
        boolean forward = (b.getId() == R.id.next_room);
        save_room_amt(activity);
        if (!game.is_done_counting(castle_pos)) {
            cur_room = next_skip(cur_room, new ROOM[]{ROOM.throne}, forward);
        } else {
            RoomNumber roomNumber = next_room_number(castle_pos, cur_room, cur_room_number, forward);
            cur_room = roomNumber.room;
            cur_room_number = roomNumber.number;
            set_room_number(activity);
        }
        set_room_amt(activity);
        game.get_castle(castle_pos).last_changed = cur_room;
        set_room_icon(activity);
    }

    public static void onDoneClick(android.widget.Button b) {
        Activity activity = (Activity) b.getContext();
        save_room_amt(activity);
        if (!game.is_done_counting(castle_pos)) {
            game.done_count(castle_pos);
            doneCountReveal(activity);
        } else {
            game.done_points(castle_pos);
            set_visibility(activity, new int[]{R.id.number, R.id.plusButton, R.id.minusButton,
                    R.id.next_room, R.id.previous_room, R.id.done_count, R.id.room,
                    R.id.roomNumber, R.id.pound, R.id.room_for_garden}, View.GONE);
            turn_on_checkmark(castle_pos, R.drawable.checkmark, true, activity);
            set_castle_score(activity, castle_pos);
            display_score(activity);
            if (game.all_castles_done())
            {
                int[] winners = game.find_winners();
                display_winners(activity, winners);
            }
        }
        OnlineRoom.UpdateCastle(castle_pos, game.get_castle(castle_pos), activity);
    }

    /**
     * if castle is done scoring, show a checkmark. else if it's occupied, show an occupied sign
     */
    public static void showCastleUsability(int castle_pos, boolean done_scoring, boolean occupied, Activity activity){
        int drawable = 0;
        if (done_scoring){
            drawable = R.drawable.checkmark;
        } else if (occupied){
            drawable =  R.drawable.occupied;
        }
        turn_on_checkmark(castle_pos, drawable, done_scoring || occupied, activity);
    }


    private static void doneCountReveal(Activity activity) {
        cur_room = firstRoom(game.get_castle(castle_pos));
        cur_room_number = 0;
        set_visibility(activity, new int[]{R.id.pound, R.id.roomNumber}, View.VISIBLE);
        set_room_icon(activity);
        set_room_number(activity);
        set_room_amt(activity);
    }

    /**
     * sets to display the number of rooms of a certain type
     * can display room points too, if counting is done
     *
     * @param activity the Activity that contains a view called "number" that we want to display on
     */
    private static void set_room_amt(Activity activity) {
        TextView numberText = activity.findViewById(R.id.number);
        switch_number_for_garden(activity);
        if (!game.is_done_counting(castle_pos)) {
            numberText.setText("" + game.get_room_amt(castle_pos, cur_room));
        } else {
            numberText.setText("" + game.get_room_pts(castle_pos, cur_room, cur_room_number));
        }
    }

    /**
     * saves the number of rooms of a certain type
     * can save room points too, if counting is done
     *
     * @param activity the Activity that contains a view called "number" that we want to extract
     */
    private static void save_room_amt(Activity activity) {
        TextView numberText = activity.findViewById(R.id.number);
        int num = Integer.parseInt(numberText.getText().toString());
        if (!game.is_done_counting(castle_pos))
            game.set_room_amt(castle_pos, cur_room, num);
        else {
            if (cur_room == ROOM.green)
                game.set_garden_room_pts(castle_pos, cur_room_number, cur_room_for_garden);
            else
                game.set_room_pts(castle_pos, cur_room, cur_room_number, num);
        }
    }


    /**
     * @return first room (type) the castle has
     */
    private static ROOM firstRoom(Castle castle) {
        ROOM res = ROOM.values()[0];
        while (castle.count_rooms(res) == 0) {
            res = next_skip(res, new ROOM[]{ROOM.blue, ROOM.fountain}, true);
        }
        return res;
    }

    private static void set_room_icon(Activity activity, ROOM room) {
        ((TextView) activity.findViewById(R.id.roomName)).setText(room.getNameId());
        ((ImageView) activity.findViewById(R.id.room)).setImageResource(room.getPicID());
    }

    private static void set_room_icon(Activity activity) {
        set_room_icon(activity, cur_room);
    }

    private static void set_room_number(Activity activity) {
        ((TextView) activity.findViewById(R.id.roomNumber)).setText("" + (cur_room_number + 1));
    }

    private static void set_castle_score(Activity activity, int castle_pos) {
        ((TextView) activity.findViewById(R.id.roomName)).setText(activity.getText(R.string.score) + ": " + game.get_castle(castle_pos).sum());
    }

    private static void display_winners(Activity activity, int[] winners){
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
    }

    private static ROOM next(ROOM room, int distance) {
        return ROOM.values()[Math.floorMod(room.ordinal() + distance, ROOM.values().length)];
    }

    private static ROOM next(ROOM room) {
        return next(room, 1);
    }

    private static ROOM prev(ROOM room) {
        return next(room, -1);
    }

    private static boolean object_in_arr(Object obj, Object[] arr) {
        for (Object o : arr) {
            if (o == obj) return true;
        }
        return false;
    }

    private static ROOM next_skip(ROOM room, ROOM[] to_skip, int distance) {
        if (to_skip.length >= ROOM.values().length) return null;
        ROOM res = next(room, distance);
        while (object_in_arr(res, to_skip)) {
            res = next(res, distance);
        }
        return res;
    }

    private static ROOM next_skip(ROOM room, ROOM[] to_skip, boolean forward) {
        return forward ? next_skip(room, to_skip, 1) : next_skip(room, to_skip, -1);
    }

    private static RoomNumber next_room_number(int castle_pos, ROOM room, int number, boolean forward) {
        ROOM res_room = room;
        int res_num = number;
        int distance = (forward ? 1 : -1);

        res_num += distance;
        if (res_num < 0 || res_num >= game.get_room_amt(castle_pos, res_room)) {
            do {
                res_room = next(res_room, distance);
            }
            while (game.get_room_amt(castle_pos, res_room) == 0 || res_room == ROOM.blue || res_room == ROOM.fountain);
            if (forward) res_num = 0;
            else res_num = game.get_room_amt(castle_pos, res_room) - 1;
        }
        return new RoomNumber(res_room, res_num);
    }

    private static void turn_on_checkmark(int castle_pos, int drawable, boolean visible, Activity activity) {
        int[] checkmarks = {R.id.checkmark1, R.id.checkmark2, R.id.checkmark3, R.id.checkmark4, R.id.checkmark5, R.id.checkmark6, R.id.checkmark7};
        ImageView checkmarkview = activity.findViewById(checkmarks[castle_pos]);
        checkmarkview.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        checkmarkview.setImageResource(drawable);
    }

    private static void set_visibility(Activity activity, int[] view_ids, int visibility) {
        for (int id : view_ids) {
            activity.findViewById(id).setVisibility(visibility);
        }
    }

    private static void display_score(Activity activity) {
        TextView score_text = activity.findViewById(R.id.scores);
        StringBuilder res = new StringBuilder("");
        for (int i = 0; i < game.get_castle_amt(); ++i) {
            res.append(activity.getString(R.string.castle));
            res.append(" ");
            res.append(activity.getString(game.get_color(i).getStringId()));
            res.append(": ");
            if (game.is_done_points(i))
                res.append(game.get_castle(i).sum());
            res.append("\n");
        }

        score_text.setText(res.toString());
    }

    private static void switch_number_for_garden(Activity activity) {
        View number = activity.findViewById(R.id.number);
        ImageView room_for_garden = activity.findViewById(R.id.room_for_garden);
        if (cur_room == ROOM.green && game.is_done_counting(castle_pos)) {
            cur_room_for_garden = game.get_green_last_changed(castle_pos, cur_room_number);
            number.setVisibility(View.INVISIBLE);
            room_for_garden.setVisibility(View.VISIBLE);
            room_for_garden.setImageResource(cur_room_for_garden.getPicID());
        } else {
            number.setVisibility(View.VISIBLE);
            room_for_garden.setVisibility(View.INVISIBLE);
        }

//        View[] views = new View[]{number, room_for_garden};
//        int i = (cur_room == ROOM.green ? 0 : 1);
//        views[i].setVisibility(View.INVISIBLE);
//        views[1-i].setVisibility(View.VISIBLE);
    }

    private static void display_next_room_for_garden(Activity activity, boolean forward) {
        ImageView room_for_garden = activity.findViewById(R.id.room_for_garden);
        cur_room_for_garden = next_skip(cur_room_for_garden, new ROOM[]{ROOM.green, ROOM.card, ROOM.attendant, ROOM.foyer, ROOM.fountain, ROOM.tower}, forward);
        room_for_garden.setImageResource(cur_room_for_garden.getPicID());
    }
}
