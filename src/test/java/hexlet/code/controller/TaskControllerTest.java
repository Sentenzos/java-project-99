package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private TaskMapper taskMapper;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private ModelCreator modelCreator;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    Task testTask;

    @BeforeEach
    public void beforeEach() {
        testTask = modelCreator.taskModel();
        var label = modelCreator.labelModel();
        labelRepository.save(label);
        testTask.setLabels(Set.of(label));
        var user = modelCreator.userModel();
        testTask.setAssignee(user);
        userRepository.save(user);
        var taskStatus = modelCreator.taskStatusModel();
        testTask.setTaskStatus(taskStatus);
        taskStatusRepository.save(taskStatus);
        token = jwt().jwt(builder -> builder.subject(userRepository.findAll().get(0).getEmail()));
    }

    @AfterEach
    public void afterEach() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testWithParams() throws Exception {
        taskRepository.save(testTask);
        var title = testTask.getName();
        var path = "/api/tasks?titleCont=" + title;
        var result = mockMvc.perform(get(path).with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("title").toString().contains(title);
    }

    @Test
    public void testShow() throws Exception {
        taskRepository.save(testTask);
        var result = mockMvc.perform(get("/api/tasks/{id}", testTask.getId()).with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        var name = testTask.getName();
        assertThatJson(body).toString().contains(name);
        taskRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCreate() throws Exception {
        var taskCreateDto = taskMapper.map(testTask);
        mockMvc.perform(
                post("/api/tasks")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(taskCreateDto))
        ). andExpect(status().isCreated());

        var name = taskCreateDto.getTitle();
        var task = taskRepository.findByName(name).get();

        assertThat(task).isNotNull();
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(task.getTaskStatus().getId()).isEqualTo(testTask.getTaskStatus().getId());
        assertThat(task.getAssignee().getId()).isEqualTo(testTask.getAssignee().getId());
        assertThat(task.getLabels().toString()).isEqualTo(testTask.getLabels().toString());
    }

    @Test
    public void testUpdate() throws Exception {
        taskRepository.save(testTask);
        var data = new TaskCreateUpdateDTO();
        var title = JsonNullable.of("new_title");
        var content = JsonNullable.of("new_content");
        data.setTitle(title);
        data.setContent(content);
        var request = put("/api/tasks/{id}", testTask.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request);

        var task = taskRepository.findById(testTask.getId());
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getName()).isEqualTo(title.get());
        assertThat(task.get().getDescription()).isEqualTo(content.get());
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
}
