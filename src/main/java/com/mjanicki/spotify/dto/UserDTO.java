package com.mjanicki.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {

    private String id, fullName, avatarUrl, email;

    private List<SongDTO> songs;
}
