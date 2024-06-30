package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    LabelRepository labelRepository;

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/labels/1").with(jwt()))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        var name = labelRepository.findAll().get(0).getName();
        assertThatJson(body).toString().contains(name);
    }

    @Test
    public void testCreate() throws Exception {
        var label = new LabelCreateDTO();
        label.setName("test_label");

        mockMvc.perform(
                post("/api/labels")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(label))
        ).andExpect(status().isCreated());

        assertThat(labelRepository.findByName("test_label").isPresent()).isTrue();
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("name", "new_label");

        var request = put("/api/labels/1").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request);

        assertThat(labelRepository.findByName("new_label").isPresent()).isTrue();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
         mockMvc.perform(get("/api/labels"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/labels/{id}", 1))
                .andExpect(status().isUnauthorized());
    }
}
