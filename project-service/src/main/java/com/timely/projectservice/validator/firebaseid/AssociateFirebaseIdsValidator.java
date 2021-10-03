package com.timely.projectservice.validator.firebaseid;

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.timely.projectservice.model.User;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssociateFirebaseIdsValidator implements ConstraintValidator<ValidAssociateFirebaseIds, Set<String>> {

    private final FirebaseAuth firebaseAuth;

    @Override
    public boolean isValid(Set<String> uids, ConstraintValidatorContext context) {
        for (String uid : uids) {
            try {
                UserRecord userRecord = firebaseAuth.getUser(uid);
                Map<String, Object> existingClaims = userRecord.getCustomClaims();
                if (!existingClaims.containsKey(User.ASSOCIATE_ROLE)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

}