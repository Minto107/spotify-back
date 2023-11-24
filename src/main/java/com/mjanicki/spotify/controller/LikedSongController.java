package com.mjanicki.spotify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
