package com.kogay.taskflow.repository;

import com.kogay.taskflow.entity.QTask;
import com.kogay.taskflow.entity.Task;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.kogay.taskflow.entity.QTask.task;
import static com.kogay.taskflow.entity.QTaskAssignee.taskAssignee;

@RequiredArgsConstructor
public class FilterTaskRepositoryImpl implements FilterTaskRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<Task> findAll(Predicate predicate, Pageable pageable) {
        List<Integer> allTasks = new JPAQuery<>(entityManager)
                .select(task.id)
                .from(task)
                .where(predicate)
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        long totalSize = allTasks.size();

        List<Integer> taskIds = allTasks.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();

        List<Task> tasks = new ArrayList<>();
        if (taskIds.size() > 0) {
            tasks.addAll(new JPAQuery<>(entityManager)
                    .select(task)
                    .from(task)
                    .innerJoin(task.owner).fetchJoin()
                    .leftJoin(task.assignees, taskAssignee).fetchJoin()
                    .leftJoin(taskAssignee.user).fetchJoin()
                    .where(task.id.in(taskIds))
                    .orderBy(getOrderSpecifiers(pageable))
                    .fetch());
        }
        return new PageImpl<>(tasks, pageable, totalSize);
    }

    private OrderSpecifier[] getOrderSpecifiers(Pageable pageable) {
        PathBuilder<QTask> entityPath = new PathBuilder<>(QTask.class, "task");
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            var orderExpression = entityPath.get(order.getProperty());
            var orderSpecifier = new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    orderExpression
            );

            orderSpecifiers.add(orderSpecifier);
        }

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}
