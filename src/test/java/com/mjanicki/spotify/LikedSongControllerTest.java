package com.mjanicki.spotify;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjanicki.spotify.dao.LikedSong;
import com.mjanicki.spotify.dao.LikedSongId;
import com.mjanicki.spotify.dao.Song;
import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.helper.UserHelper;
import com.mjanicki.spotify.repository.LikedSongsRepository;
import com.mjanicki.spotify.repository.SongRepository;
import com.mjanicki.spotify.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class LikedSongControllerTest {
    
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private HttpServletRequest request;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            // .apply(springSecurity())
            .build();

        Mockito.when(passwordEncoder.encode(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenAnswer(i -> i.getArguments()[0].equals(i.getArguments()[1]));

        user1 = new User("1", "TEST", null, "test@gmail.com", passwordEncoder.encode("test"), null, null, null);

        song1 = new Song(1, "Test song", "Test author", "/", "/", user1, null, null);
        song2 = new Song(2, "New song", "Test author", "/", "/", user1, null, null);
        song3 = new Song(3, "New song2", "Test author", "/", "/", user1, null, null);

        ArrayList<Song> songs = new ArrayList<>(Arrays.asList(song1, song2));

        likedSong1 = LikedSong.builder().id(LikedSongId.builder().idSong(song1.getId()).idUser(user1.getId()).build()).song(song1).user(user1).build();
        likedSong2 = LikedSong.builder().id(LikedSongId.builder().idSong(song2.getId()).idUser(user1.getId()).build()).song(song2).user(user1).build();
        
        
        ArrayList<LikedSong> likedSongs = new ArrayList<>(Arrays.asList(likedSong1, likedSong2));

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user1));
        Mockito.when(songRepository.findAll()).thenReturn(songs);
        when(userHelper.getUser(any(HttpServletRequest.class))).thenReturn(user1);
        Mockito.when(likedSongsRepository.findByUser(user1)).thenReturn(likedSongs);
    }

    @Autowired
    ObjectMapper mapper;

    @MockBean
    SongRepository songRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    LikedSongsRepository likedSongsRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    UserHelper userHelper;

    User user1;

    Song song1;
    Song song2;
    Song song3;

    LikedSong likedSong1;
    LikedSong likedSong2;

    @Test
    public void getLikedSongsForUser_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/likedsongs")
                .requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void isLiked_success_true() throws Exception {
        final var id = 2;
        when(likedSongsRepository.findById(LikedSongId.builder().idSong(id).idUser(user1.getId()).build())).thenReturn(Optional.of(likedSong2));
        mockMvc.perform(MockMvcRequestBuilders.get("/likedsongs/" + id)
                .requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$", Matchers.is(true)));
    }

    @Test
    public void isLiked_success_false() throws Exception {
        final var id = 3;
        mockMvc.perform(MockMvcRequestBuilders.get("/likedsongs/" + id)
                .requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$", Matchers.is(false)));
    }

    @Test
    public void handleLike_success_removeLike() throws Exception {
        final var id = 2;
        when(likedSongsRepository.findById(LikedSongId.builder().idSong(id).idUser(user1.getId()).build())).thenReturn(Optional.of(likedSong2));
        mockMvc.perform(MockMvcRequestBuilders.get("/likedsongs/handleLike/" + id)
                .requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$", Matchers.is(false)));
    }

    @Test
    public void handleLike_success_addLike() throws Exception {
        final var id = song3.getId();
        when(songRepository.findById(id)).thenReturn(Optional.of(song3));
        mockMvc.perform(MockMvcRequestBuilders.get("/likedsongs/handleLike/" + id)
                .requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$", Matchers.is(true)));
    }
}
