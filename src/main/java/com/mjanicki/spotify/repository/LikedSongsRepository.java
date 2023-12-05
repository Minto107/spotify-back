package com.mjanicki.spotify.repository;

import com.mjanicki.spotify.dao.LikedSong;
import com.mjanicki.spotify.dao.LikedSongId;
import com.mjanicki.spotify.dao.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedSongsRepository extends JpaRepository<LikedSong, LikedSongId> {
    
    List<LikedSong> findByUser(User user);

    // LikedSong findByUserAndId(User user, LikedSongId id);
}
