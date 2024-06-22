package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
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
public class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    TaskStatusRepository taskStatusRepository;

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses/1").with(jwt()))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        var name = taskStatusRepository.findAll().get(0).getName();
        assertThatJson(body).toString().contains(name);
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("name", "test_name");
        data.put("slug", "test_slug");


        var request = post("/api/task_statuses").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(taskStatusRepository.findBySlug("test_slug").isPresent()).isTrue();

        taskStatusRepository.deleteAll();
    }

    @Test
    public void testUpdate() throws Exception {
        //TODO создание статуса в отдельный метод
        var taskStatus = new TaskStatus();
        taskStatus.setName("test_name");
        taskStatus.setSlug("test_slug");
        taskStatusRepository.save(taskStatus);

        var taskStatusId = taskStatusRepository.findBySlug("test_slug").get().getId();

        var data = new HashMap<>();
        data.put("slug", "new_slug");

        var request = put("/api/task_statuses/" + taskStatusId).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request);

        assertThat(taskStatusRepository.findBySlug("test_slug").get().getName()).isEqualTo("test_name");

        taskStatusRepository.deleteAll();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses/{id}", 1))
                .andExpect(status().isUnauthorized());
    }
}
