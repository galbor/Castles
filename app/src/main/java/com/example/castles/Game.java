package com.example.castles;

public class Game {
    public enum COLOR {
        yellow(R.drawable.castle_yellow, R.string.yellow),
        red(R.drawable.castle_red, R.string.red),
        white(R.drawable.castle_beige, R.string.white),
        green(R.drawable.castle_green, R.string.green),
        brown(R.drawable.castle_brown, R.string.brown),
        lilac(R.drawable.castle_lilac, R.string.lilac),
        blue(R.drawable.castle_blue, R.string.blue);

        private final int drawableId;
        private final int stringId;
        COLOR(int drawableId, int stringId){
            this.drawableId = drawableId;
            this.stringId = stringId;
        }
        int getDrawableId() {return this.drawableId;}
        int getStringId() {return this.stringId;}
    }

    private final COLOR[] castle_order;
    private Castle[] castles;

    public Game(COLOR[] castle_order) {
        this.castle_order = castle_order;
        castles = new Castle[castle_order.length];
        for (int i = 0; i< castle_order.length; ++i) {
            castles[i] = new Castle();
        }
    }

    public Castle get_castle(int castle_pos){
        return castles[castle_pos];
    }
    public void set_castle(int castle_pos, Castle castle) {castles[castle_pos] = castle;}
    public COLOR get_color(int castle_pos){
        return castle_order[castle_pos];
    }

    public int get_castle_amt() {return castles.length;}

    public void set_room_amt(int castle_pos, ROOM room, int amt) {
        castles[castle_pos].set_room_amount(room, amt);
    }

    public void set_room_amt(COLOR castle, ROOM room, int amt) {
        set_room_amt(find_castle_pos(castle), room, amt);
    }

    public int get_room_amt(int castle_pos, ROOM room){
        return castles[castle_pos].get_room_amount(room);
    }

    public void set_room_pts(int castle_pos, ROOM room, int index, int pts){
        castles[castle_pos].set_room_points(room, index, pts);
    }

    public void set_garden_room_pts(int castle_pos, int index, ROOM room){
        castles[castle_pos].set_garden_points(index, room);
    }

    public int get_room_pts(int castle_pos, ROOM room, int index){
        return castles[castle_pos].get_room_points(room, index);
    }

    public int[] find_winners() {
        int[] sums = new int[castles.length];
        int[] players_pts = new int[castles.length]; //a player has his and the next castle
        // points are min*20000 + max*20 + specialty rooms
        int max_pts = 0;
        int[] players_with_max = new int[castles.length];
        int cnt_players_with_max = 0;
        for (int i = 0; i < sums.length; ++i) {
            sums[i] = castles[i].sum();
        }
        for (int i = 0; i < players_pts.length; ++i) {
            players_pts[i] = Math.min(sums[i], sums[next(i)]) * 20000
                    + Math.max(sums[i], sums[next(i)]) * 20;
            players_pts[i] += castles[i].count_specialties() +
                    castles[next(i)].count_specialties();
            if (players_pts[i] > max_pts) max_pts = players_pts[i];
        }
        for (int i = 0; i < players_pts.length; ++i) {
            if (players_pts[i] == max_pts) {
                players_with_max[cnt_players_with_max++] = i;
            }
        }

        return shorten_array(players_with_max, cnt_players_with_max);
    }

    public boolean is_done_counting(int castle_pos){
        return castles[castle_pos].done_counting != READYLEVEL.unready;
    }

    public void done_count(int castle_pos){
        castles[castle_pos].init_points();
        castles[castle_pos].done_counting = READYLEVEL.donecount;
    }

    public boolean is_done_points(int castle_pos){return castles[castle_pos].done_counting == READYLEVEL.done;}

    public void done_points(int castle_pos){
        // I think I forgot a line of code here lmao
        castles[castle_pos].done_counting = READYLEVEL.done;
    }

    public boolean all_castles_done(){
        for (Castle castle : castles){
            if (castle.done_counting != READYLEVEL.done) return false;
        }
        return true;
    }

    public ROOM get_green_last_changed(int castle_pos, int index){
        return castles[castle_pos].get_green_last_selected(index);
    }


    private int[] shorten_array(int[] arr, int len){
        int[] res = new int[len];
        System.arraycopy(arr, 0, res, 0, len);
        return res;
    }
    private int next(int i) {
        return (i + 1) % castles.length;
    }

    private int find_castle_pos(COLOR color) {
        for (int i = 0; i < castle_order.length; ++i) {
            if (castle_order[i] == color)
                return i;
        }
        throw new IllegalArgumentException("no such color in this game " + color);
    }
}
