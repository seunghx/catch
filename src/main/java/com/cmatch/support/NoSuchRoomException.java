package com.cmatch.support;

public class NoSuchRoomException extends RuntimeException {

    private static final long serialVersionUID = -7155851579939555637L;

    public NoSuchRoomException(String message) {
        super(message);
    }
}
