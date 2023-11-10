package com.kogay.taskflow.util;

import com.kogay.taskflow.entity.Task;
import com.kogay.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskAccessChecker {

    private final TaskRepository taskRepository;

    public boolean isCurrentUserOwner(Integer taskId) {
        return taskRepository.findById(taskId)
                .filter(TaskAccessChecker::isCurrentUserOwner)
                .isPresent();
    }

    private static boolean isCurrentUserOwner(Task task) {
        String currentUsername = getCurrentUsername();
        String ownerUsername = getOwnerUsername(task);

        return currentUsername.equals(ownerUsername);
    }

    private static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        return principal.getUsername();
    }

    private static String getOwnerUsername(Task task) {
        return task.getOwner().getUsername();
    }
}