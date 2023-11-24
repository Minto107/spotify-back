package com.mjanicki.spotify.exception;

public class SongNotFoundException extends RuntimeException{

    public SongNotFoundException(String title) {
        super("Couldn't find a song with title: " + title);
    }

    public SongNotFoundException(Integer id) {
        super("Couldn't find a song with id: " + id);
    }

    public SongNotFoundException() {
        super("Incorrect id/title passed!");
    }
}
