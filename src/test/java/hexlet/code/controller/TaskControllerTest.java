package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskStatusRepository taskStatusRepository;

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var task = createTask();
        taskRepository.save(task);
        var result = mockMvc.perform(get("/api/tasks/1").with(jwt()))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        var name = taskRepository.findAll().get(0).getName();
        assertThatJson(body).toString().contains(name);

        taskRepository.deleteAll();
    }

    @Test
    public void testCreate() throws Exception {
        TaskStatus status = taskStatusRepository.findBySlug("draft").get();
        TaskCreateDTO taskToSave = new TaskCreateDTO();
        taskToSave.setTitle("Test_Name");
        taskToSave.setStatus(status.getSlug());

        mockMvc.perform(
                post("/api/tasks")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(taskToSave))
        ).andExpect(status().isCreated());

        assertThat(taskRepository.findByName("Test_Name").isPresent()).isTrue();
        taskRepository.deleteAll();
    }

    @Test
    public void testUpdate() throws Exception {
        var task = createTask();
        taskRepository.save(task);

        var data = new HashMap<>();
        data.put("title", "new_title");
        data.put("content", "new_content");

        var request = put("/api/tasks/1").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request);

        assertThat(taskRepository.findByName("new_title").isPresent()).isTrue();

        taskRepository.deleteAll();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
         mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    private Task createTask() {
        var task = new Task();
        task.setDescription("task_description");
        task.setName("task_name");
        task.setTaskStatus(taskStatusRepository.findAll().get(0));
        task.setAssignee(userRepository.findAll().get(0));
        return task;
    }
}
