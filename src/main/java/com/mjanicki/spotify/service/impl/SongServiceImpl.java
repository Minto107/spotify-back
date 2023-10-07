package com.mjanicki.spotify.service.impl;

import com.mjanicki.spotify.dao.Song;
import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.dto.SongDTO;
import com.mjanicki.spotify.dto.UserDTO;
import com.mjanicki.spotify.exception.SongNotFoundException;
import com.mjanicki.spotify.repository.SongRepository;
import com.mjanicki.spotify.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Autowired
    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public ResponseEntity<List<SongDTO>> getSongs() {
        //TODO create DTO objects
        List<SongDTO> songs = new ArrayList<>();

        songRepository.findAll().forEach(e -> {
            User tmp = e.getUser();
            UserDTO userDTO = new UserDTO(tmp.getId(), tmp.getFullName(), tmp.getAvatarUrl(), tmp.getEmail(), null);
            songs.add(new SongDTO(e.getId(), e.getTitle(), e.getAuthor(), e.getSongPath(), e.getImagePath(), userDTO));
        });

        return ResponseEntity.ok(songs);
    }

    @Override
    public ResponseEntity<SongDTO> getSongByTitle(String title) {
        //TODO parse as DTO
        Song song = songRepository.findByTitle(title)
                .orElseThrow(() -> new SongNotFoundException(title));

        User tmp = song.getUser();
        UserDTO userDTO = new UserDTO(tmp.getId(), tmp.getFullName(), tmp.getAvatarUrl(), tmp.getEmail(), null);

        return ResponseEntity.ok(new SongDTO(song.getId(), song.getTitle(), song.getAuthor(), song.getSongPath(), song.getImagePath(), userDTO));
    }
}
