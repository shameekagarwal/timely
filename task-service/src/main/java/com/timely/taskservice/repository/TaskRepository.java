package com.timely.taskservice.repository;

import java.util.List;

import com.timely.taskservice.model.Task;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByProjectId(String projectId);

}
