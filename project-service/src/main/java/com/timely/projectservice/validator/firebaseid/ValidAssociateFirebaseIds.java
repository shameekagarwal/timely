package com.timely.projectservice.validator.firebaseid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = AssociateFirebaseIdsValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAssociateFirebaseIds {

    public String message() default "contains invalid associate ids";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}
