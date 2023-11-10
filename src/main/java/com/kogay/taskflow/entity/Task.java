package com.kogay.taskflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "assignees")
@EqualsAndHashCode(of = "name")
@Builder
@Audited
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User owner;

    @OneToMany(mappedBy = "task",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    private List<TaskAssignee> assignees = new ArrayList<>();

    public void addAssignee(User user) {
        var taskAssignee = new TaskAssignee();
        taskAssignee.setTask(this);
        taskAssignee.setUser(user);
        taskAssignee.setAssignmentDate(LocalDateTime.now());

        assignees.add(taskAssignee);
    }

    public boolean removeAssignee(User user) {
        return assignees.removeIf(taskAssignee -> taskAssignee.getUser().equals(user));
    }
}
