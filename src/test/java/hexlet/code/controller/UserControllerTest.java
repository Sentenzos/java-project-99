package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelCreator modelCreator;

    @Autowired
    UserMapper userMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    User testUser;

    @BeforeEach
    public void beforeEach() {
        testUser = modelCreator.userModel();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(userRepository.findAll().get(0).getEmail()));
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);
        var result = mockMvc.perform(get("/api/users/{id}", testUser.getId()).with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).toString().contains(testUser.getEmail());
    }

    @Test
    public void testCreate() throws Exception {
        var user = modelCreator.userModel();
        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(userRepository.findByEmail(user.getEmail()).isPresent()).isTrue();
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new UserUpdateDTO();
        var email = JsonNullable.of("new@email.com");
        data.setEmail(email);

        var request = put("/api/users/{id}", testUser.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request);

        assertThat(userRepository.findByEmail(email.get()).isPresent()).isTrue();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(status().isUnauthorized());
    }
}
