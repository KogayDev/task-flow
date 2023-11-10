package com.kogay.taskflow.repository;

import com.kogay.taskflow.entity.Task;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterTaskRepository {
    Page<Task> findAll(Predicate predicate, Pageable pageable);
}
