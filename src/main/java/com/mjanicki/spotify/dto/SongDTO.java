package com.mjanicki.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SongDTO {

    private Integer id;

    private String title, author, songPath, imagePath;

    private UserDTO user;
}
