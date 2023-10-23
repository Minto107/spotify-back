package com.mjanicki.spotify.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.mjanicki.spotify.dto.LikedSongDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface LikedSongService {
    
    ResponseEntity<List<LikedSongDTO>> getLikedSongsForUser(HttpServletRequest request);
}
