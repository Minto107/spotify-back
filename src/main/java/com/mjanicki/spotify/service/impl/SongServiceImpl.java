package com.mjanicki.spotify.service.impl;

import com.mjanicki.spotify.dao.Song;
import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.dto.SongDTO;
import com.mjanicki.spotify.dto.UserDTO;
import com.mjanicki.spotify.helper.UserHelper;
import com.mjanicki.spotify.repository.SongRepository;
import com.mjanicki.spotify.service.SongService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    private final UserHelper userHelper;

    @Autowired
    public SongServiceImpl(SongRepository songRepository, UserHelper userHelper) {
        this.songRepository = songRepository;
        this.userHelper = userHelper;
    }

    @Override
    public ResponseEntity<List<SongDTO>> getSongs() {
        List<SongDTO> songs = new ArrayList<>();

        songRepository.findAll().forEach(e -> {
            User tmp = e.getUser();
            UserDTO userDTO = new UserDTO(tmp.getId(), tmp.getFullName(), tmp.getAvatarUrl(), tmp.getEmail(), null);
            songs.add(new SongDTO(e.getId(), e.getTitle(), e.getAuthor(), e.getSongPath(), e.getImagePath(), userDTO));
        });
        log.info(new StringBuilder().append("Found ").append(songs.size()).append(" songs.").toString());
        return ResponseEntity.ok(songs);
    }

    @Override
    public ResponseEntity<List<SongDTO>> getSongByTitle(String title) {
        if (title.equals("") || title.length() == 0)
            return getSongs();
        // Song song = songRepository.findByTitle(title)
        //         .orElseThrow(() -> new SongNotFoundException(title));

        List<Song> songs = songRepository.findByTitleIgnoreCaseContains(title);

        List<SongDTO> dtos = new ArrayList<>();
        songs.forEach((e) -> {
            User tmp = e.getUser();
            UserDTO userDTO = UserDTO.builder().id(tmp.getId()).fullName(tmp.getFullName())
                                .avatarUrl(tmp.getAvatarUrl()).email(tmp.getEmail()).songs(null).build();
            dtos.add(SongDTO.builder().id(e.getId()).title(e.getTitle()).author(e.getAuthor())
                        .songPath(e.getSongPath()).imagePath(e.getImagePath()).user(userDTO).build());
        });

        // User tmp = song.getUser();
        // UserDTO userDTO = new UserDTO(tmp.getId(), tmp.getFullName(), tmp.getAvatarUrl(), tmp.getEmail(), null);

        // return ResponseEntity.ok(new SongDTO(song.getId(), song.getTitle(), song.getAuthor(), song.getSongPath(), song.getImagePath(), userDTO));
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<List<SongDTO>> getUserSongs(HttpServletRequest request) {
        final var user = userHelper.getUser(request);
        if (user == null) {
            log.warn("Request to get user songs was received, but no access token was provided with the request. Returning empty list.");
            return ResponseEntity.ok(new ArrayList<>());
        }
        final var userDTO = UserDTO.builder().id(user.getId()).fullName(user.getFullName())
                            .avatarUrl(user.getAvatarUrl()).email(user.getEmail()).songs(null).build();

        final var userSongs = songRepository.findByUser(user);

        List<SongDTO> songs = new ArrayList<>();

        userSongs.forEach(song -> {
            songs.add(SongDTO.builder().id(song.getId()).title(song.getTitle())
                        .author(song.getAuthor()).songPath(song.getSongPath()).imagePath(song.getImagePath())
                        .user(userDTO).build());
        });
        return ResponseEntity.ok(songs);
    }
}
