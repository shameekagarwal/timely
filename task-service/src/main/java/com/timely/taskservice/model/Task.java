package com.timely.taskservice.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.timely.taskservice.constant.TaskStatus;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Task {

    @Id
    private String id;

    @NotEmpty(message = "task title cannot be empty")
    @Size(min = 5, max = 25, message = "task title should be between 5 and 25 characters long")
    private String title;

    @Size(min = 5, max = 100, message = "task description should be between 5 and 100 characters long")
    private String description;

    @NotNull
    private TaskStatus taskStatus;

    @DBRef
    private Project project;

}
