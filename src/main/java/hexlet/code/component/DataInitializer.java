package hexlet.code.component;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
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
    private final UserService userService;
    @Autowired
    private final TaskStatusService taskStatusService;
    @Autowired
    private final LabelService labelService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //TODO создание сущностей в отдельные методы
        // и не через сервис создавать, а через репозиторий (свериться с приложением хекслет)
        var email = "hexlet@example.com";
        var userData = new UserCreateDTO();
        userData.setEmail(email);
        userData.setPassword("qwerty");
        userService.create(userData);

        var taskStatus1 = new TaskStatusCreateDTO();
        taskStatus1.setName("draft");
        taskStatus1.setSlug("draft");
        taskStatusService.create(taskStatus1);

        var taskStatus2 = new TaskStatusCreateDTO();
        taskStatus2.setName("to_review");
        taskStatus2.setSlug("to_review");
        taskStatusService.create(taskStatus2);

        var taskStatus3 = new TaskStatusCreateDTO();
        taskStatus3.setName("to_be_fixed");
        taskStatus3.setSlug("to_be_fixed");
        taskStatusService.create(taskStatus3);

        var taskStatus4 = new TaskStatusCreateDTO();
        taskStatus4.setName("to_publish");
        taskStatus4.setSlug("to_publish");
        taskStatusService.create(taskStatus4);

        var taskStatus5 = new TaskStatusCreateDTO();
        taskStatus5.setName("published");
        taskStatus5.setSlug("published");
        taskStatusService.create(taskStatus5);

        var label1 = new LabelCreateDTO();
        label1.setName("feature");
        labelService.create(label1);

        var label2 = new LabelCreateDTO();
        label2.setName("bug");
        labelService.create(label2);
    }
}
