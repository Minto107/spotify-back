package com.mjanicki.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {

    private Integer id;

    private String title, author, songPath, imagePath;

    private UserDTO user;
}
