package com.hacking.demows.dto;

import com.hacking.demows.models.Movement;

public class TransferResponse {
    boolean success;
    Movement movement;

    public TransferResponse() {

    }
    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }
}
