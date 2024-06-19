package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/users/1").with(jwt()))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).toString().contains("hexlet@example.com");
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("firstName", "Ruslan");
        data.put("lastName", "Nek");
        data.put("email", "test@email.com");
        data.put("password", "12345678");


        var request = post("/api/users").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(userRepository.findByEmail("test@email.com").isPresent()).isTrue();

        userRepository.deleteAll();
    }

    @Test
    public void testUpdate() throws Exception {
        //TODO создание юзера в отдельный метод
        var user = new User();
        user.setFirstName("Ruslan");
        user.setLastName("Nek");
        user.setEmail("test@email.com");
        user.setPassword("12345678");
        userRepository.save(user);

        var userId = userRepository.findByEmail("test@email.com").get().getId();

        var data = new HashMap<>();
        data.put("email", "new@email.com");
        data.put("password", "87654321");

        var request = put("/api/users/" + userId).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request);

        assertThat(userRepository.findByEmail("new@email.com").isPresent()).isTrue();

        userRepository.deleteAll();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        var response = mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        var response = mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(status().isUnauthorized());
    }
}
