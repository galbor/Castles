package com.example.castles;

import java.util.ArrayList;

public class Castle {
    private int[] rooms;
    private int[][] points;
    private ROOM[] green_room_effects;
    public ROOM last_changed;

    public Castle(){
        rooms = new int[ROOM.values().length];
        points = new int[ROOM.values().length][];
        last_changed = ROOM.values()[0];

        rooms[ROOM.throne.ordinal()] = 1;
    }

    public int sum(){
        int res = 0;
        for (int[] arr : points){
            {
                for (int pts : arr)
                    res += pts;
            }

        }
        return res;
    }

    /**
     * sets the number of points for blue rooms to be the number of rooms, times 4 if has all colors
     */
    public void set_blue_points(){
        int pts_per_room = has_all_colors() ? 4 : 1;
        points[ROOM.blue.ordinal()][0]= rooms[ROOM.blue.ordinal()] * pts_per_room;
    }

    /**
     * sets the number of points for fountains to be their amount times 5
     */
    public void set_fountain_points(){
        points[ROOM.fountain.ordinal()][0] = rooms[ROOM.fountain.ordinal()]*5;
    }

    public void set_garden_points(int index, ROOM room){
        int score = (room == ROOM.throne ? count_specialties() : rooms[room.ordinal()]);
        set_room_points(ROOM.green, index, score);
        green_room_effects[index] = room;
    }

    public void set_room_amount(ROOM room, int amt){
        assert room != ROOM.throne;
        rooms[room.ordinal()] = amt;
    }

    public int get_room_amount(ROOM room){
        return rooms[room.ordinal()];
    }

    public void set_room_points(ROOM room, int index, int pts){
        assert index >=0 && index < points[room.ordinal()].length;
        points[room.ordinal()][index]= pts;
    }

    public int get_room_points(ROOM room, int index){
        assert index >=0 && index < points[room.ordinal()].length;
        return points[room.ordinal()][index];
    }

    public int count_specialties(){
        int cnt = 0;
        cnt += rooms[ROOM.tower.ordinal()];
        cnt += rooms[ROOM.fountain.ordinal()];
        cnt += rooms[ROOM.foyer.ordinal()];
        cnt += rooms[ROOM.throne.ordinal()];
        return cnt;
    }

    /**
     * @return amount of rooms in the castle of room type
     */
    public int count_rooms(ROOM room){
        return rooms[room.ordinal()];
    }

    /**
     * generates the points matrix
     */
    public void init_points(){
        for (int i = 0; i < points.length; ++i){
            if (i == ROOM.blue.ordinal() || i == ROOM.fountain.ordinal()) {
                points[i] = new int[1];
            }
            else {
                points[i] = new int[rooms[i]];
                for (int j = 0; j<rooms[i]; ++j)
                    points[i][j] = 0;
            }
        }
        //pretending all gardens are yellow by default
        green_room_effects = new ROOM[rooms[ROOM.green.ordinal()]];
        for (int i = 0; i < green_room_effects.length; ++i){
            ROOM room = ROOM.values()[0];
            green_room_effects[i] = room;
            set_garden_points(i, room);
        }
        set_blue_points();
        set_fountain_points();
    }

    public ROOM get_green_last_selected(int index){
        return green_room_effects[index];
    }

    private boolean has_all_colors(){
        for (int i = 0; i <= ROOM.black.ordinal(); ++i){
            if (i != ROOM.blue.ordinal() && rooms[i] <= 0) return false;
        }
        return true;
    }
}
