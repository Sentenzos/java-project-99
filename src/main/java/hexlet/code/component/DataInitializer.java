package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    UserService userService;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        createUser("hexlet@example.com", "qwerty");
        createTaskStatus("draft", "draft");
        createTaskStatus("to_review", "to_review");
        createTaskStatus("to_be_fixed", "to_be_fixed");
        createTaskStatus("to_publish", "to_publish");
        createTaskStatus("published", "published");
        createLabel("feature");
        createLabel("bug");
    }

    private void createUser(String email, String password) {
        var user = new UserCreateDTO();
        user.setEmail(email);
        user.setPassword(password);
        userService.create(user);
    }

    private void createTaskStatus(String name, String slug) {
        var taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        taskStatusRepository.save(taskStatus);
    }

    private void createLabel(String name) {
        var label = new Label();
        label.setName(name);
        labelRepository.save(label);
    }
}



