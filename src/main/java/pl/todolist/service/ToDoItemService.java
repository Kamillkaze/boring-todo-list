package pl.todolist.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.todolist.exception.DuplicatedValueOfUniqueFieldException;
import pl.todolist.model.ToDoItem;
import pl.todolist.dto.ToDoItemDto;
import pl.todolist.repository.ToDoItemRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ToDoItemService {

    private final ToDoItemRepository toDoItemRepository;

    public ToDoItemService(ToDoItemRepository toDoItemRepository) {
        this.toDoItemRepository = toDoItemRepository;
    }

    public List<ToDoItemDto> getAllToDoItems() {
        return toDoItemRepository.findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(ToDoItem::getDeadline))
                                    .map(ToDoItemDto::new)
                                    .collect(Collectors.toList());
    }

    public ToDoItemDto addToDoItem(ToDoItemDto item) {
        if (toDoItemRepository.existsByShortDescription(item.getShortDescription())) {
            throw new DuplicatedValueOfUniqueFieldException(item.getShortDescription());
        }
        
        ToDoItem toBeSaved = new ToDoItem(item);
        ToDoItem saved = toDoItemRepository.save(toBeSaved);
        return new ToDoItemDto(saved);
    }

    @Transactional
    public long deleteToDoItem(ToDoItemDto item) {
        return toDoItemRepository.deleteByShortDescription(item.getShortDescription());
    }

    public ToDoItemDto updateToDoItem(int id, ToDoItemDto update){
        Optional<ToDoItem> toDoItemFromDB = toDoItemRepository.findById(id);

        if (toDoItemFromDB.isPresent()) {
            ToDoItem item = toDoItemFromDB.get();

            item.setShortDescription(update.getShortDescription());
            item.setDetails(update.getDetails());
            item.setDeadline(update.getDeadline());

            toDoItemRepository.save(item);
            return new ToDoItemDto(item);
        }
        return null;
    }
}
