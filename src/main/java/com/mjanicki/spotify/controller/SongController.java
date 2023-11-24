package com.mjanicki.spotify.controller;

import com.mjanicki.spotify.dto.SongDTO;
import com.mjanicki.spotify.service.SongService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> findByTitle(@PathVariable("title") String title) {
        return songService.getSongByTitle(title);
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<?> oneById(@PathVariable("id") Integer id) {
        return songService.getSongById(id);
    }

    @GetMapping("/user")
    public ResponseEntity<?> user(HttpServletRequest request) {
        return songService.getUserSongs(request);
    }

    @GetMapping("/songMp3/{id}")
    public ResponseEntity<?> mp3(@PathVariable("id") Integer id) {
        return songService.getSongFile(id);
    }

    @GetMapping("/songArt/{id}")
    public ResponseEntity<?> img(@PathVariable("id") Integer id) {
        return songService.getSongArt(id);
    }
    
    @PostMapping(value = "", consumes = {"*/*"})
    public ResponseEntity<?> add(HttpServletRequest request, @RequestPart("mp3") MultipartFile mp3, @RequestPart("img") MultipartFile img, @RequestPart("dto") SongDTO dto) {
        return songService.addSong(dto, mp3, img, request);
    }
}
