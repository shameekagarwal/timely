package com.timely.userservice.dto;

import javax.validation.constraints.Pattern;

import com.timely.userservice.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoleRequestDto {

    @Pattern(regexp = "^(" + User.ASSOCIATE_ROLE + "|" + User.MANAGER_ROLE + ")$", message = "role should be either "
            + User.ASSOCIATE_ROLE + " or " + User.MANAGER_ROLE)
    private String role;

}
