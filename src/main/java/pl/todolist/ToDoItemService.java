package pl.todolist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ToDoItemService {

    @Autowired
    ToDoItemRepository toDoItemRepository;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/todoitems")
    public ResponseEntity getToDoItems() throws JsonProcessingException {
        List<ToDoItem> toDoItems = toDoItemRepository.findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(ToDoItem::getDeadline))
                                    .collect(Collectors.toList());
        return ResponseEntity.ok(objectMapper.writeValueAsString(toDoItems));
    }

    @PostMapping("/todoitems")
    public ResponseEntity addToDoItem(@RequestBody ToDoItem item) {
        Optional<ToDoItem> toDoItemFromDB = toDoItemRepository.findByShortDescription(item.getShortDescription());

        if (toDoItemFromDB.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        ToDoItem saved = toDoItemRepository.save(item);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/todoitems")
    public ResponseEntity deleteToDoItem(@RequestBody ToDoItem item) {
        Optional<ToDoItem> toDoItemFromDB = toDoItemRepository.findByShortDescription(item.getShortDescription());

        if (toDoItemFromDB.isPresent()) {
            toDoItemRepository.delete(toDoItemFromDB.get());
            return ResponseEntity.ok(toDoItemFromDB);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/todoitems/{id}")
    public ResponseEntity updateToDoItem(@PathVariable(value = "id") int id, @RequestBody ToDoItem update){
        Optional<ToDoItem> toDoItemFromDB = toDoItemRepository.findById(id);

        if (toDoItemFromDB.isPresent()) {
            ToDoItem item = toDoItemFromDB.get();

            item.setShortDescription(update.getShortDescription());
            item.setDetails(update.getDetails());
            item.setDeadline(update.getDeadline());

            toDoItemRepository.save(item);
            return ResponseEntity.ok(item);
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
}
