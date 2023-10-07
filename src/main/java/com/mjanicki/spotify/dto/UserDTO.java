package com.mjanicki.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {

    private String id, fullName, avatarUrl, email;

    private List<SongDTO> songs;
}
