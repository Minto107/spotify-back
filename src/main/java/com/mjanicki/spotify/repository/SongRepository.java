package com.mjanicki.spotify.repository;

import com.mjanicki.spotify.dao.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    Optional<Song> findByTitle(String title);
}
