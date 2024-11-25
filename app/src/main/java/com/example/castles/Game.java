package com.example.castles;

import java.util.LinkedList;
import java.util.List;

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

    public final COLOR[] castle_order;
    private Castle[] castles;

    public Game(COLOR[] castle_order) {
        this.castle_order = castle_order;
        castles = new Castle[castle_order.length];
        for (int i = 0; i< castle_order.length; ++i) {
            castles[i] = new Castle();
        }
    }

    /**
     *
     * @return castle_order as ordinals
     */
    public List<Number> CastleOrderOrdinals(){
        List<Number> res = new LinkedList<>();
        for (COLOR color : castle_order) {
            res.add(color.ordinal());
        }
        return res;
    }

    public static COLOR[] CastleOrderFromOrdinals(List<Number> castle_order_ordinals){
        COLOR[] castle_order = new COLOR[castle_order_ordinals.size()];
        for (int i = 0; i< castle_order.length; i++){
            COLOR x = COLOR.values()[castle_order_ordinals.get(i).intValue()];
            castle_order[i] = x;
        }
        return castle_order;
    }

    public Castle GetCastle(int castle_pos){
        return castles[castle_pos];
    }
    public void SetCastle(int castle_pos, Castle castle) {castles[castle_pos] = castle;}
    public COLOR GetColor(int castle_pos){
        return castle_order[castle_pos];
    }

    public int GetCastleAmt() {return castles.length;}

    public void SetRoomAmt(int castle_pos, ROOM room, int amt) {
        castles[castle_pos].SetRoomAmount(room, amt);
    }

    public int GetRoomAmt(int castle_pos, ROOM room){
        return castles[castle_pos].GetRoomAmount(room);
    }

    public void SetRoomPoints(int castle_pos, ROOM room, int index, int pts){
        castles[castle_pos].SetRoomPoints(room, index, pts);
    }

    public void SetGardenRoomPoints(int castle_pos, int index, ROOM room){
        castles[castle_pos].SetGardenPoints(index, room);
    }

    public int GetRoomPoints(int castle_pos, ROOM room, int index){
        return castles[castle_pos].GetRoomPoints(room, index);
    }

    /**
     * @return array of winning players
     */
    public int[] FindWinners() {
        int[] sums = new int[castles.length];
        int[] players_pts = new int[castles.length]; //a player has his and the next castle
        // points are min*20000 + max*20 + specialty rooms
        int max_pts = 0;
        int[] players_with_max = new int[castles.length];
        int cnt_players_with_max = 0;
        for (int i = 0; i < sums.length; ++i) {
            sums[i] = castles[i].PointsSum();
        }
        for (int i = 0; i < players_pts.length; ++i) {
            players_pts[i] = Math.min(sums[i], sums[NextCastleIndex(i)]) * 20000
                    + Math.max(sums[i], sums[NextCastleIndex(i)]) * 20;
            players_pts[i] += castles[i].CountSpecialties() +
                    castles[NextCastleIndex(i)].CountSpecialties();
            if (players_pts[i] > max_pts) max_pts = players_pts[i];
        }
        for (int i = 0; i < players_pts.length; ++i) {
            if (players_pts[i] == max_pts) {
                players_with_max[cnt_players_with_max++] = i;
            }
        }

        return ShortenArray(players_with_max, cnt_players_with_max);
    }

    public boolean IsDoneCounting(int castle_pos){
        return castles[castle_pos].readylevel != READYLEVEL.unready;
    }

    /**
     * sets the castle's ready level to donecount
     */
    public void DoneCount(int castle_pos){
        castles[castle_pos].InitPoints();
        castles[castle_pos].readylevel = READYLEVEL.donecount;
    }

    public boolean IsDoneScoring(int castle_pos){return castles[castle_pos].readylevel == READYLEVEL.donescoring;}

    /**
     * sets the castle's ready level to donescoring
     */
    public void DoneScoring(int castle_pos){
        castles[castle_pos].readylevel = READYLEVEL.donescoring;
    }

    /**
     * @return true iff all castle's have a donescoring ready level
     */
    public boolean AllCastlesDone(){
        for (Castle castle : castles){
            if (castle.readylevel != READYLEVEL.donescoring) return false;
        }
        return true;
    }

    public ROOM GetGreenLastChanged(int castle_pos, int index){
        return castles[castle_pos].GetGreenLastSelected(index);
    }


    /**
     * @return copy of arr with length len
     */
    private int[] ShortenArray(int[] arr, int len){
        int[] res = new int[len];
        System.arraycopy(arr, 0, res, 0, len);
        return res;
    }

    private int NextCastleIndex(int i) {
        return (i + 1) % castles.length;
    }
}
