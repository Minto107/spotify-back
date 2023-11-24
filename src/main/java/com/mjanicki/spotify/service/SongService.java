package com.mjanicki.spotify.service;

import com.mjanicki.spotify.dto.SongDTO;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongService {
    ResponseEntity<List<SongDTO>> getSongs();

    ResponseEntity<List<SongDTO>> getSongByTitle(String title);

    ResponseEntity<SongDTO> getSongById(Integer id);

    ResponseEntity<List<SongDTO>> getUserSongs(HttpServletRequest request);

    ResponseEntity<?> getSongFile(Integer id);

    ResponseEntity<?> getSongArt(Integer id);

    ResponseEntity<?> addSong(SongDTO dto, MultipartFile mp3, MultipartFile img, HttpServletRequest request);
}
