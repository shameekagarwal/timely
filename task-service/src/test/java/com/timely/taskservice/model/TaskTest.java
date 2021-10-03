package com.timely.taskservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.timely.taskservice.constant.TaskStatus;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenInvalidTitle_thenFailure() {
        Task task = new Task();
        task.setDescription(RandomStringUtils.randomAlphabetic(10));
        task.setTaskStatus(TaskStatus.DONE);

        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        assertEquals("title", extractFirstViolationField(violations));
        assertEquals(1, violations.size());
    }

    @Test
    public void whenInvalidDescription_thenFailure() {
        Task task = new Task();
        task.setTitle(RandomStringUtils.randomAlphabetic(10));
        task.setDescription(RandomStringUtils.randomAlphabetic(105));
        task.setTaskStatus(TaskStatus.DONE);

        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        assertEquals("description", extractFirstViolationField(violations));
        assertEquals(1, violations.size());
    }

    @Test
    public void whenInvalidTaskStatus_thenFailure() {
        Task task = new Task();
        task.setTitle(RandomStringUtils.randomAlphabetic(10));

        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        assertEquals("taskStatus", extractFirstViolationField(violations));
        assertEquals(1, violations.size());
    }

    @Test
    public void whenValid_thenSuccess() {
        Task task = new Task();
        task.setTitle(RandomStringUtils.randomAlphabetic(10));
        task.setTaskStatus(TaskStatus.DONE);

        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        assertTrue(violations.isEmpty());
    }

    private String extractFirstViolationField(Set<ConstraintViolation<Task>> violations) {
        return violations.stream().collect(Collectors.toList()).get(0).getPropertyPath().toString();
    }

}
