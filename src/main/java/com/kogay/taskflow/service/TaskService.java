package com.kogay.taskflow.service;

import com.kogay.taskflow.dto.TaskCreateEditDto;
import com.kogay.taskflow.dto.TaskFilter;
import com.kogay.taskflow.dto.TaskReadDto;
import com.kogay.taskflow.entity.Status;
import com.kogay.taskflow.entity.Task;
import com.kogay.taskflow.entity.User;
import com.kogay.taskflow.exception.TaskAlreadyExistsException;
import com.kogay.taskflow.exception.TaskNotFoundException;
import com.kogay.taskflow.exception.UserAlreadyAssignedException;
import com.kogay.taskflow.exception.UserNotFoundException;
import com.kogay.taskflow.repository.TaskRepository;
import com.kogay.taskflow.repository.UserRepository;
import com.kogay.taskflow.util.QPredicates;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.kogay.taskflow.entity.QTask.task;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public TaskReadDto create(TaskCreateEditDto taskCreateEditDto) {
        try {
            return Optional.of(taskCreateEditDto)
                    .map(dto -> modelMapper.map(dto, Task.class))
                    .map(this::setTaskOwner)
                    .map(taskRepository::save)
                    .map(task -> modelMapper.map(task, TaskReadDto.class))
                    .orElseThrow();
        } catch (DataIntegrityViolationException ex) {
            throw new TaskAlreadyExistsException(taskCreateEditDto.getName());
        }
    }

    private Task setTaskOwner(Task task) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username)
                .ifPresent(task::setOwner);
        return task;
    }

    public Optional<TaskReadDto> findById(Integer id) {
        return taskRepository.findById(id)
                .map(task -> modelMapper.map(task, TaskReadDto.class));
    }

    public Page<TaskReadDto> findAll(TaskFilter taskFilter,
                                     Pageable pageable) {
        QPredicates predicates = QPredicates.builder()
                .add(taskFilter.assigneeIds(), task.assignees.any().user.id::in)
                .add(taskFilter.status(), task.status::eq)
                .add(taskFilter.ownerId(), task.owner.id::eq);

        return taskRepository.findAll(predicates.buildAnd(), pageable)
                .map(task -> modelMapper.map(task, TaskReadDto.class));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or @taskAccessChecker.isCurrentUserOwner(#id)")
    public Optional<TaskReadDto> update(Integer id, TaskCreateEditDto taskCreateEditDto) {
        try {
            return taskRepository.findById(id)
                    .map(task -> {
                        modelMapper.map(taskCreateEditDto, task);
                        return task;
                    })
                    .map(taskRepository::saveAndFlush)
                    .map(task -> modelMapper.map(task, TaskReadDto.class));
        } catch (DataIntegrityViolationException ex) {
            throw new TaskAlreadyExistsException(taskCreateEditDto.getName());
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or @taskAccessChecker.isCurrentUserOwner(#id)")
    public boolean delete(Integer id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    taskRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or @taskAccessChecker.isCurrentUserOwner(#id)")
    public TaskReadDto changeStatus(Integer id, Status newStatus) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setStatus(newStatus);
                    return task;
                })
                .map(taskRepository::saveAndFlush)
                .map(task -> modelMapper.map(task, TaskReadDto.class))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or @taskAccessChecker.isCurrentUserOwner(#taskId)")
    public TaskReadDto addAssigneeToTask(Integer taskId, Integer assigneeUserId) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));

            User assignee = userRepository.findById(assigneeUserId)
                    .orElseThrow(() -> new UserNotFoundException(assigneeUserId));

            task.addAssignee(assignee);

            return modelMapper.map(taskRepository.save(task), TaskReadDto.class);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyAssignedException();
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or @taskAccessChecker.isCurrentUserOwner(#taskId)")
    public boolean removeAssigneeFromTask(Integer taskId, Integer assigneeUserId) {
        Optional<Task> maybeTask = taskRepository.findById(taskId);
        Optional<User> maybeUser = userRepository.findById(assigneeUserId);

        if (maybeTask.isEmpty() || maybeUser.isEmpty()) {
            return false;
        }

        Task task = maybeTask.get();
        User user = maybeUser.get();
        return task.removeAssignee(user);
    }
}
