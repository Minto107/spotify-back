package com.mjanicki.spotify;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjanicki.spotify.dao.Song;
import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.dao.security.LoginRequest;
import com.mjanicki.spotify.dao.security.RegisterRequest;
import com.mjanicki.spotify.repository.SongRepository;
import com.mjanicki.spotify.repository.UserRepository;

@SpringBootTest
public class AuthControllerTest {
    
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

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
        song2 = new Song(1, "New song", "Test author", "/", "/", user1, null, null);

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user1));
    }

    @Autowired
    ObjectMapper mapper;

    @MockBean
    SongRepository songRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PasswordEncoder passwordEncoder;


    User user1;

    Song song1;
    Song song2;
    
    @Test
    public void login_success() throws Exception {
        final LoginRequest loginRequest = LoginRequest.builder().email(user1.getEmail()).password("test").build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .content(this.mapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", Matchers.notNullValue()))
                .andExpect(jsonPath("$.user.fullName", Matchers.is(user1.getFullName())));
    }

    @Test
    public void register_success() throws Exception {
        final RegisterRequest registerRequest = RegisterRequest.builder().email("test@abc.com").fullName("Example").password("ABC").build();

        final String requestBody = this.mapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$.user.fullName", Matchers.is(registerRequest.getFullName())))
                .andExpect(jsonPath("$.token", Matchers.notNullValue()));
    }

    @Test
    public void logout_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/logout"))
                .andExpect(status().isOk());
    }
}
