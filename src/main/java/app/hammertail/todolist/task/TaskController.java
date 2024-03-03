package app.hammertail.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    if (currentDate.isAfter(taskModel.getStartAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The start date must be greater than the current date.");
    }

    if (taskModel.getEndAt().isBefore(taskModel.getStartAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The end date must be greater than the start date.");
    }

    TaskModel task = this.taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.OK).body(task);
  }
}
