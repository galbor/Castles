package com.example.castles;

import java.util.HashSet;
import java.util.Set;

/**
 * join, create, etc with online rooms
 */
public class OnlineRoom {

    /**
     *
     * @param game
     * @return the password for the game
     */
    public static String GeneratePassword(Game game){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<game.get_castle_amt(); i++){
            sb.append(game.get_color_from_pos(i).ordinal());
        }

        return sb.toString();
    }

    /**
     *
     * @param password
     * @return null if the password doesn't correspond to a game. the game if it does
     */
    public static Game GetGame(String password){
        Game.COLOR[] castle_order = VarifyPassword(password);
        return castle_order != null ? new Game(castle_order) : null;
    }


    /**
     * varifies password
     * @param password
     * @return the game's castle_order if the password is correct. otherwise null
     */
    private static Game.COLOR[] VarifyPassword(String password){
        Game.COLOR[] castle_order = new Game.COLOR[password.length()];
        Set<Integer> colors = new HashSet<>();
        if (password.length() < 3 || password.length() > Game.COLOR.values().length) return null;
        for (int i = 0; i<password.length(); i++){
            int castle = password.charAt(i)-'0';
            if (castle < 0 || castle >= Game.COLOR.values().length) return null;
            castle_order[i] = Game.COLOR.values()[castle];
            if (colors.contains(castle)) return null;
            colors.add(castle);
        }
        return castle_order;
    }
}
