package com.timely.userservice.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.timely.userservice.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AddRoleRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenValid_thenSuccess() {

        AddRoleRequestDto requestDtoOne = new AddRoleRequestDto(User.ASSOCIATE_ROLE);
        Set<ConstraintViolation<AddRoleRequestDto>> violationsOne = validator.validate(requestDtoOne);
        assertEquals(0, violationsOne.size());

        AddRoleRequestDto requestDtoTwo = new AddRoleRequestDto(User.MANAGER_ROLE);
        Set<ConstraintViolation<AddRoleRequestDto>> violationsTwo = validator.validate(requestDtoTwo);
        assertEquals(0, violationsTwo.size());

    }

    @Test
    public void whenInvalid_thenFailure() {
        AddRoleRequestDto requestDto = new AddRoleRequestDto("ROLE_ADMIN");
        Set<ConstraintViolation<AddRoleRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
    }

}
