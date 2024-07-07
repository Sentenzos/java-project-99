package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @Autowired
    ModelCreator modelCreator;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskStatusMapper taskStatusMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    TaskStatus testTaskStatus;
    User testUser;

    @BeforeEach
    public void beforeEach() {
        testTaskStatus = modelCreator.taskStatusModel();
        testUser = modelCreator.userModel();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    public void afterEach() {
        taskStatusRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var result = mockMvc.perform(get("/api/task_statuses/{id}", testTaskStatus.getId())
                        .with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        var name = testTaskStatus.getName();
        assertThatJson(body).toString().contains(name);
    }

    @Test
    public void testCreate() throws Exception {
        var taskStatus = taskStatusMapper.map(testTaskStatus);

        var request = post("/api/task_statuses").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatus));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(taskStatusRepository.findBySlug(taskStatus.getSlug()).isPresent()).isTrue();
    }

    @Test
    public void testUpdate() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var taskStatusId = testTaskStatus.getId();

        var data = new TaskStatusUpdateDTO();
        var name = "new_name";
        data.setName(name);

        var request = put("/api/task_statuses/" + taskStatusId)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request);

        assertThat(taskStatusRepository.findById(taskStatusId).get().getName()).isEqualTo(name);
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses/{id}", 1))
                .andExpect(status().isUnauthorized());
    }
}
