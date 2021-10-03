package com.timely.projectservice.repository;

import java.util.List;
import java.util.Optional;

import com.timely.projectservice.model.Project;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findByAssociatesIdsContainingOrManagerId(String associateId, String managerId);

    Optional<Project> findByIdAndManagerId(String projectId, String managerId);

}
