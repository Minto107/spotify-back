package com.mjanicki.spotify.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Builder
@Table(name = "liked_songs")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LikedSong {

    @EmbeddedId
    LikedSongId id = new LikedSongId();

    @ManyToOne
    @MapsId("idSong")
    @JoinColumn(name = "id_song")
    private Song song;

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "id_user")
    private User user;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private Timestamp createdAt;

    public LikedSong(Song song, User user) {
        this.song = song;
        this.user = user;
    }
}
