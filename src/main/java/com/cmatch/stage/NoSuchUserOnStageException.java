package com.cmatch.stage;

public class NoSuchUserOnStageException extends RuntimeException {

    private static final long serialVersionUID = -49800207387376684L;
    
    public NoSuchUserOnStageException(String message) {
        super(message);
    }
    
    public NoSuchUserOnStageException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
