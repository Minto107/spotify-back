package com.mjanicki.spotify.service;

import com.mjanicki.spotify.dto.SongDTO;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SongService {
    ResponseEntity<List<SongDTO>> getSongs();

    ResponseEntity<List<SongDTO>> getSongByTitle(String title);

    ResponseEntity<List<SongDTO>> getUserSongs(HttpServletRequest request);
}
