package com.kogay.taskflow.repository;

import com.kogay.taskflow.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TaskRepository extends
        JpaRepository<Task, Integer>,
        PagingAndSortingRepository<Task, Integer>,
        FilterTaskRepository {

    @EntityGraph(attributePaths = {"owner", "assignees.user"})
    Optional<Task> findById(Integer id);
}
