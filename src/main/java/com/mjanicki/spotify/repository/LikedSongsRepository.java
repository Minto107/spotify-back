package com.mjanicki.spotify.repository;

import com.mjanicki.spotify.dao.LikedSong;
import com.mjanicki.spotify.dao.LikedSongId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedSongsRepository extends JpaRepository<LikedSong, LikedSongId> {
}
