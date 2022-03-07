package com.hacking.demows.models;

public class Movement {
    protected long id;
    
    public Movement(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}