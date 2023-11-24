package com.mjanicki.spotify.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    //TODO later add authors
    private String author;

    @Column(nullable = false, name = "song_path")
    private String songPath;

    @Column(nullable = false, name = "image_path")
    private String imagePath;

    @JoinColumn(nullable = false, name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private Timestamp updatedAt;

    public Song(String title, String author, String songPath, String imagePath, User user) {
        this.title = title;
        this.author = author;
        this.songPath = songPath;
        this.imagePath = imagePath;
        this.user = user;
    }
}
