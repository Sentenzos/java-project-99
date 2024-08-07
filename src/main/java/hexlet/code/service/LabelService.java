package hexlet.code.service;

import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelCreateUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    LabelMapper labelMapper;

    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelRepository.findAll()
                .stream()
                .map(l -> labelMapper.map(l))
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    public LabelDTO show(Long id) {
        var task = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return labelMapper.map(task);
    }

    public LabelDTO create(LabelCreateUpdateDTO data) {
        var label = labelMapper.map(data);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(Long id, LabelCreateUpdateDTO data) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        labelMapper.update(data, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void delete(Long id) {
        labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        labelRepository.deleteById(id);
    }
}
