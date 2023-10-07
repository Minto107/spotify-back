package com.mjanicki.spotify.exception;

public class SongNotFoundException extends RuntimeException{

    public SongNotFoundException(String title) {
        super("Couldn't find a song with title: " + title);
    }
}
