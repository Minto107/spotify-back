package com.mjanicki.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikedSongDTO {
    
    @NonNull
    private SongDTO song;

    @NonNull
    private UserDTO user;
}
