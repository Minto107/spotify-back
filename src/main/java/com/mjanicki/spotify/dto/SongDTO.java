package com.mjanicki.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SongDTO {

    private Integer id;

    private String title, author, songPath, imagePath;

    private UserDTO user;
}
