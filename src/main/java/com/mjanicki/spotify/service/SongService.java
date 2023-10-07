package com.mjanicki.spotify.service;

import com.mjanicki.spotify.dto.SongDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SongService {
    ResponseEntity<List<SongDTO>> getSongs();

    ResponseEntity<SongDTO> getSongByTitle(String title);
}
