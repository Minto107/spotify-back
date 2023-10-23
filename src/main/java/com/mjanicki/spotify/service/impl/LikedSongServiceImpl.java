package com.mjanicki.spotify.service.impl;

import com.mjanicki.spotify.dao.LikedSong;
import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.dto.LikedSongDTO;
import com.mjanicki.spotify.dto.SongDTO;
import com.mjanicki.spotify.dto.UserDTO;
import com.mjanicki.spotify.helper.UserHelper;
import com.mjanicki.spotify.repository.LikedSongsRepository;
import com.mjanicki.spotify.service.LikedSongService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LikedSongServiceImpl implements LikedSongService {

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private LikedSongsRepository likedSongsRepository;

    @Override
    public ResponseEntity<List<LikedSongDTO>> getLikedSongsForUser(HttpServletRequest request) {
        User user = userHelper.getUser(request);

        List<LikedSong> likedSongs = likedSongsRepository.findByUser(user);

        UserDTO userDTO = UserDTO.builder().id(user.getId()).fullName(user.getFullName())
                            .avatarUrl(user.getAvatarUrl()).email(user.getEmail()).songs(null).build();

        List<LikedSongDTO> dtos = new ArrayList<>();
        likedSongs.forEach(e -> {
            SongDTO song = SongDTO.builder().author(e.getSong().getAuthor()).id(e.getSong().getId())
                            .title(e.getSong().getTitle()).songPath(e.getSong().getSongPath())
                            .imagePath(e.getSong().getImagePath()).user(userDTO).build();
            dtos.add(LikedSongDTO.builder().song(song).user(userDTO).build());
        });

        return ResponseEntity.ok(dtos);
    }
}
