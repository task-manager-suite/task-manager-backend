package org.montadhahri.taskmanager.repository;

import org.montadhahri.taskmanager.enumeration.TaskStatus;
import org.montadhahri.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByIsEnabledTrue();

    List<Task> findByStatusAndIsEnabledTrue(TaskStatus status);

    Optional<Task> findByIdAndIsEnabledTrue(Long id);

    Optional<Task> findByTitleAndIsEnabledTrue(String title);
}
