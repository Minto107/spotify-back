package com.mjanicki.spotify.service.impl;

import com.mjanicki.spotify.dao.Song;
import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.dto.SongDTO;
import com.mjanicki.spotify.dto.UserDTO;
import com.mjanicki.spotify.exception.SongNotFoundException;
import com.mjanicki.spotify.helper.UserHelper;
import com.mjanicki.spotify.repository.SongRepository;
import com.mjanicki.spotify.service.SongService;
import com.mjanicki.spotify.service.StorageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    private final UserHelper userHelper;

    private final StorageService storageService;

    @Autowired
    public SongServiceImpl(SongRepository songRepository, UserHelper userHelper, StorageService storageService) {
        this.songRepository = songRepository;
        this.userHelper = userHelper;
        this.storageService = storageService;
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
    public ResponseEntity<SongDTO> getSongById(Integer id) {
        if (id <= 0)
            throw new SongNotFoundException();

        final Song song = songRepository.findById(id)
                            .orElseThrow(() -> new SongNotFoundException(id));

        final User tmp = song.getUser();
        final UserDTO userDTO = UserDTO.builder().id(tmp.getId()).fullName(tmp.getFullName())
                            .avatarUrl(tmp.getAvatarUrl()).email(tmp.getEmail()).songs(null).build();
       
        final SongDTO dto = SongDTO.builder().id(song.getId()).title(song.getTitle()).author(song.getAuthor())
                        .songPath(song.getSongPath()).imagePath(song.getImagePath()).user(userDTO).build();

        return ResponseEntity.ok(dto);
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

    @Override
    public ResponseEntity<?> getSongFile(Integer id) {
        final String songPath = songRepository.findById(id)
                                    .orElseThrow(() -> new SongNotFoundException(id)).getSongPath();
        final File mp3File = new File(songPath);
        try {
            if (mp3File.exists()) {
                final byte[] mp3Arr = Files.readAllBytes(mp3File.toPath());
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Disposition", "attachment; filename=\\" + mp3File.getName());
                final ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(mp3Arr, headers, HttpStatus.OK);

                return response;
        }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<?> getSongArt(Integer id) {
        final String imgPath = songRepository.findById(id)
                                    .orElseThrow(() -> new SongNotFoundException(id)).getImagePath();
        final File imgFile = new File(imgPath);

        try {
            if (imgFile.exists()) {
                final byte[] imgArr = Files.readAllBytes(imgFile.toPath());
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Disposition", "attachment; filename=\\" + imgFile.getName());
                final ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(imgArr, headers, HttpStatus.OK);

                return response;
        }
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<?> addSong(SongDTO dto, MultipartFile mp3, MultipartFile img, HttpServletRequest request) {
        final String imgName = UUID.randomUUID().toString() + ".jpg";
        final String mp3Name = UUID.randomUUID().toString() + ".mp3";

        final String imgStorage = storageService.save(img, "img", imgName);
        final String mp3Storage = storageService.save(mp3, "mp3", mp3Name);

        Song song = Song.builder().title(dto.getTitle()).author(dto.getAuthor())
                        .songPath(mp3Storage).imagePath(imgStorage)
                        .user(userHelper.getUser(request)).build();

        songRepository.save(song);

        return ResponseEntity.ok("OK");
    }
}
