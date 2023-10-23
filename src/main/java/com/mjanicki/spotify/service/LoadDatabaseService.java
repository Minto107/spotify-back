package com.mjanicki.spotify.service;

import com.mjanicki.spotify.dao.LikedSong;
import com.mjanicki.spotify.dao.Song;
import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.repository.LikedSongsRepository;
import com.mjanicki.spotify.repository.SongRepository;
import com.mjanicki.spotify.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Log4j2
public class LoadDatabaseService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    CommandLineRunner initDatabase(SongRepository songRepository, UserRepository userRepository, LikedSongsRepository likedSongsRepository) {
        log.info("Preloaded: " + userRepository.save(new User("test user", "test@gmail.com", passwordEncoder.encode("abcdefu"))));
        log.info("Preloaded: " + userRepository.save(new User("test user2", "test2@gmail.com", passwordEncoder.encode("qwerty123"))));

        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(e -> {
            log.info(e.toString());
            users.add(e);
        });

        log.info("Preloaded: " + songRepository.save(new Song("Fade", "Alan Walker", "fake path", "fake path", users.get(0))));
        log.info("Preloaded: " + songRepository.save(new Song("Ignite", "Alan Walker", "fake path", "fake path", users.get(1))));

        log.info("Preloaded: " + likedSongsRepository.save(new LikedSong(songRepository.findByTitle("Fade").isPresent() ? songRepository.findByTitle("Fade").get() : null, users.get(0))));
        log.info("Preloaded: " + likedSongsRepository.save(new LikedSong(songRepository.findByTitle("Ignite").isPresent() ? songRepository.findByTitle("Ignite").get() : null, users.get(1))));

        return null;
    }

}
