package app.hammertail.todolist.task;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
  public ResponseEntity<TaskModel> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    System.out.println("chegou no controller" + request.getAttribute("idUser"));
    var idUser = (UUID) request.getAttribute("idUser");
    taskModel.setIdUser(idUser);
    this.taskRepository.save(taskModel);
    return ResponseEntity.status(201).body(taskModel);
  }
}
