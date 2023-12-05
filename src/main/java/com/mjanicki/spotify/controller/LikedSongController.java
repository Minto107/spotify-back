package com.mjanicki.spotify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mjanicki.spotify.service.LikedSongService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("likedsongs")
public class LikedSongController {

    @Autowired
    LikedSongService service;

    @GetMapping("")
    public ResponseEntity<?> all(HttpServletRequest request) {
        return service.getLikedSongsForUser(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> isLiked(HttpServletRequest request, @PathVariable("id") Integer id) {
        return service.isLiked(request, id);
    }

    @GetMapping("/handleLike/{id}")
    public ResponseEntity<?> handleLike(HttpServletRequest request, @PathVariable("id") Integer id) {
        return service.handleLike(request, id);
    }
}
