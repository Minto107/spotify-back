package com.mjanicki.spotify.controller;

import com.mjanicki.spotify.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("songs")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("")
    public ResponseEntity<?> all() {
        return songService.getSongs();
    }

    @GetMapping("/{title}")
    public ResponseEntity<?> one(@PathVariable("title") String title) {
        return songService.getSongByTitle(title);
    }
}
