package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @Autowired
    ModelCreator modelCreator;
    @Autowired
    LabelMapper labelMapper;
    @Autowired
    UserRepository userRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    Label testLabel;

    User testUser;

    @BeforeEach
    public void beforeEach() {
        testLabel = modelCreator.labelModel();
        testUser = modelCreator.userModel();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    public void afterEach() {
        labelRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        labelRepository.save(testLabel);
        var result = mockMvc.perform(get("/api/labels/{id}", testLabel.getId()).with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        var name = testLabel.getName();
        assertThatJson(body).toString().contains(name);
    }

    @Test
    public void testCreate() throws Exception {
        var label = labelMapper.map(testLabel);

        mockMvc.perform(
                post("/api/labels")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(label))
        ).andExpect(status().isCreated());

        assertThat(labelRepository.findByName(label.getName()).isPresent()).isTrue();
    }

    @Test
    public void testUpdate() throws Exception {
        labelRepository.save(testLabel);
        var data = new LabelUpdateDTO();
        var label = "new_label";
        data.setName(label);

        var request = put("/api/labels/{id}", testLabel.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request);
        assertThat(labelRepository.findByName(label).isPresent()).isTrue();
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
