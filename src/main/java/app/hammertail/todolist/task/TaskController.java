package app.hammertail.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.hammertail.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    UUID idUser = (UUID) request.getAttribute("idUser");
    taskModel.setIdUser(idUser);

    LocalDateTime currentDate = LocalDateTime.now();

    if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The start/end date must be greater than the current date.");
    }

    if (taskModel.getEndAt().isBefore(taskModel.getStartAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The end date must be greater than the start date.");
    }

    TaskModel task = this.taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.OK).body(task);
  }

  @GetMapping("/")
  public ResponseEntity<?> list(HttpServletRequest request) {

    UUID idUser = (UUID) request.getAttribute("idUser");
    List<TaskModel> tasks = this.taskRepository.findByIdUser(idUser);

    return ResponseEntity.status(HttpStatus.OK).body(tasks);
  }

  @PutMapping("/{id}")
  public TaskModel update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
    UUID idUser = (UUID) request.getAttribute("idUser");

    TaskModel task = this.taskRepository.findById(id).orElse(null);

    Utils.copyNonNullProperties(taskModel, task);

    taskModel.setIdUser(idUser);
    taskModel.setId(id);
    return this.taskRepository.save(taskModel);
  }
}
