package com.example.castles;

public enum ROOM{
    yellow(R.string.yellow_room, R.drawable.room_yellow),
    orange(R.string.orange_room, R.drawable.room_orange),
    purple(R.string.purple_room, R.drawable.room_purple),
    blue(R.string.blue_room, R.drawable.room_blue),
    green(R.string.green_room, R.drawable.room_green),
    gray(R.string.gray_room, R.drawable.room_gray),
    black(R.string.black_room, R.drawable.room_black),
    tower(R.string.tower_room, R.drawable.room_tower),
    foyer(R.string.foyer_room, R.drawable.room_foyer),
    fountain(R.string.fountain_room, R.drawable.room_fountain),
    card(R.string.card_room, R.drawable.room_card),
    attendant(R.string.attendant_room, R.drawable.room_attendent),
    throne(R.string.throne_room, R.drawable.room_throne);

    private final int nameId;
    private final int picID;

    ROOM(int name, int pic){
        this.nameId = name;
        this.picID = pic;
    }

    int getNameId(){return this.nameId;}
    int getPicID(){return this.picID;}
}
//black must be last color for has_all_colors() to work