package com.timely.projectservice.model;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.timely.projectservice.validator.firebaseid.ValidAssociateFirebaseIds;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Project {

    @Id
    private String id;

    @NotNull(message = "project title cannot be empty")
    @NotEmpty(message = "project title cannot be empty")
    @Size(min = 5, max = 25, message = "project title should be between 5 and 25 characters long")
    private String title;

    private String managerId;

    @ValidAssociateFirebaseIds
    private Set<String> associatesIds;

}
