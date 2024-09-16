package com.example.castles;

public enum READYLEVEL {
    unready(false, false),
    donecount(false, true),
    done(true, true);


    private final boolean first;
    private final boolean second;
    READYLEVEL(boolean first, boolean second){
        this.first=first; this.second=second;
    }
}
