package com.mjanicki.spotify.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikedSongId implements Serializable {

    @Column(name = "id_song", nullable = false)
    private Integer idSong;

    @Column(name = "id_user", nullable = false)
    private String idUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikedSongId that = (LikedSongId) o;
        return Objects.equals(idSong, that.idSong) && Objects.equals(idUser, that.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSong, idUser);
    }
}
