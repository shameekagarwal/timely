package com.timely.taskservice.repository;

import java.util.List;
import java.util.Optional;

import com.timely.taskservice.model.Project;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findByAssociatesIdsContainingOrManagerId(String associateId, String managerId);

    Optional<Project> findByIdAndAssociatesIdsContaining(String id, String userId);

}