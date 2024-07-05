package hexlet.code.specification;

import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> {
            return titleCont == null ? cb.conjunction() : cb.like(root.get("name".toLowerCase()), titleCont.toLowerCase());
        };
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> {
            return assigneeId == null ? cb.conjunction() : cb.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) -> {
            return status == null ? cb.conjunction() : cb.equal(root.get("taskStatus").get("slug"), status);
        };
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            return labelId == null ? cb.conjunction() : cb.equal(root.get("labels").get("id"), labelId);
        };
    }
}
