package com.example.castles;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Castle {
    private int[] rooms;
    private int[][] points;
    private ROOM[] green_room_effects;
    public ROOM last_changed;

    public READYLEVEL done_counting;

    public Castle(){
        rooms = new int[ROOM.values().length];
        points = new int[ROOM.values().length][0];
        last_changed = ROOM.values()[0];

        rooms[ROOM.throne.ordinal()] = 1;
        green_room_effects = new ROOM[0];

        done_counting = READYLEVEL.unready;
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

    //bad usage of strings "magic values"
    public Map<String, Object> ToMap(){
        Map<String, Object> res = new HashMap<>();

        List<Number> roomslist = new LinkedList<>();
        for (int room : rooms) roomslist.add(room);
        res.put("rooms", roomslist);

        for (int i = 0; i<points.length; i++){
            List<Number> pointslist = new LinkedList<>();
            for (int room : points[i]) pointslist.add(room);
            res.put("points" + i, pointslist);
        }

        res.put("readylevel", done_counting.ordinal());
        res.put("last_changed", last_changed.ordinal());

        List<Number> green_effects = new LinkedList<>();
        for (int i = 0; i< green_room_effects.length; i++){
            green_effects.add(green_room_effects[i].ordinal());
        }
        res.put("green_room_effects", green_effects);

        return res;
    }

    /**
     * @param map created with ToMap()
     * @return castle that was created by ToMap()
     */
    public static Castle FromMap(Map<String, Object> map){
        Castle res = new Castle();
        List<Number> roomslist = (List<Number>) map.get("rooms");

        for (int i = 0; i<roomslist.size(); i++){
            res.rooms[i] = roomslist.get(i).intValue();
        }


        for (int i = 0; i<res.rooms.length; i++){
            List<Number> pointslist = (List<Number>) map.get("points" + i);
            res.points[i] = new int[pointslist.size()];
            for (int j = 0; j<pointslist.size(); j++) res.points[i][j] = pointslist.get(j).intValue();
        }


        res.done_counting = READYLEVEL.values ()[((Number) map.get("readylevel")).intValue()];
        res.last_changed = ROOM.values()[((Number) map.get("last_changed")).intValue()];

        List<Number> green_effects = (List<Number>) map.get("green_room_effects");
        res.green_room_effects = new ROOM[green_effects.size()];
        for (int i = 0; i< green_effects.size(); i++){
            res.green_room_effects[i] = ROOM.values()[green_effects.get(i).intValue()];
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
