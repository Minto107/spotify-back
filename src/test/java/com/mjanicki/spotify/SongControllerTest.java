package com.mjanicki.spotify;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjanicki.spotify.dao.Song;
import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.dto.SongDTO;
import com.mjanicki.spotify.dto.UserDTO;
import com.mjanicki.spotify.repository.SongRepository;
import com.mjanicki.spotify.repository.UserRepository;

@SpringBootTest()
public class SongControllerTest {
    
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            // .apply(springSecurity())
            .build();
    }

    @Autowired
    ObjectMapper mapper;

    @MockBean
    SongRepository songRepository;

    @MockBean
    UserRepository userRepository;

    User user1 = new User("1", "TEST", null, "test@gmail.com",  "test", null, null, null);

    Song song1 = new Song(1, "Test song", "Test author", "/", "/", user1, null, null);
    Song song2 = new Song(1, "New song", "Test author", "/", "/", user1, null, null);
    
    @Test
    public void getAllSongs_success() throws Exception {
        List<Song> songs = new ArrayList<>(Arrays.asList(song1, song2));

        Mockito.when(songRepository.findAll()).thenReturn(songs);

        mockMvc.perform(MockMvcRequestBuilders.get("/songs")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$[0].title", is("Test song")));
    }    

    @Test
    public void getSongByName_success() throws Exception {
        List<Song> songs = new ArrayList<>(Arrays.asList(song1, song2));

        Mockito.when(songRepository.findByTitleIgnoreCaseContains("song")).thenReturn(songs);

        mockMvc.perform(MockMvcRequestBuilders.get("/songs/song")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)));
    }    

    @Test
    public void getSongById_success() throws Exception {
        final var song = song2;
        Mockito.when(songRepository.findById(song.getId())).thenReturn(Optional.of(song));

        mockMvc.perform(MockMvcRequestBuilders.get("/songs/byId/" + song.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", notNullValue()))
                        .andExpect(jsonPath("$.id", is(song.getId())));
    }   

    @Test
    public void getSongById_notFound() throws Exception {
        final var id = 3;

        mockMvc.perform(MockMvcRequestBuilders.get("/songs/byId/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
                        }

    @Test
    public void createSong_success() throws Exception {
        UserDTO userDTO = UserDTO.builder().id(user1.getId()).fullName(user1.getFullName()).email(user1.getEmail())
                        .avatarUrl(user1.getAvatarUrl()).build();
        SongDTO songDTO = SongDTO.builder().title("Test").author("Test").songPath("/").imagePath("/").user(userDTO).build();

        Song song = Song.builder().title(songDTO.getTitle()).author(songDTO.getAuthor()).songPath(songDTO.getSongPath()).imagePath(songDTO.getImagePath()).user(user1).build();

        Mockito.when(songRepository.save(song)).thenReturn(song);

        MockMultipartFile mockMp3File = new MockMultipartFile("mp3", "audio.mp3", "multipart/form-data", "audio".getBytes());
        MockMultipartFile mockImgFile = new MockMultipartFile("img", "img.jpg", "multipart/form-data", "img".getBytes());
        MockMultipartFile mockDtoFile = new MockMultipartFile("dto", "", "application/json", this.mapper.writeValueAsString(songDTO).getBytes());

        MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders.multipart("/songs")
                                    .file(mockImgFile).file(mockMp3File)
                                    .file(mockDtoFile)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON);
                                    // .content(this.mapper.writeValueAsString(songDTO));

        mockMvc.perform(mockReq)
                    .andExpect(status().isOk()).andExpect(jsonPath("$", notNullValue()))
                    .andExpect(jsonPath("$", is("OK")));
    }
    
    @Test
    public void createSong_badRequest() throws Exception {
        UserDTO userDTO = UserDTO.builder().id(user1.getId()).fullName(user1.getFullName()).email(user1.getEmail())
                        .avatarUrl(user1.getAvatarUrl()).build();
        SongDTO songDTO = SongDTO.builder().title("Test").author("Test").songPath("/").imagePath("/").user(userDTO).build();

        Song song = Song.builder().title(songDTO.getTitle()).author(songDTO.getAuthor()).songPath(songDTO.getSongPath()).imagePath(songDTO.getImagePath()).user(user1).build();

        Mockito.when(songRepository.save(song)).thenReturn(song);

        MockMultipartFile mockDtoFile = new MockMultipartFile("dto", "", "application/json", this.mapper.writeValueAsString(songDTO).getBytes());

        MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders.multipart("/songs")
                                    .file(mockDtoFile)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockReq)
                    .andExpect(status().isBadRequest());
    }
}
