package com.timely.userservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import com.timely.userservice.dto.AddRoleRequestDto;
import com.timely.userservice.dto.GetUserDto;
import com.timely.userservice.filter.FirebaseFilter;
import com.timely.userservice.model.User;
import com.timely.userservice.util.TrackExecutionTime;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final FirebaseAuth firebaseAuth;

    @GetMapping("ping")
    @TrackExecutionTime
    public String ping() {
        return "pong from user-service";
    }

    @PutMapping
    @TrackExecutionTime
    public String addRole(@RequestBody @Valid AddRoleRequestDto requestBody, 
            HttpServletRequest request) throws Exception {
        UserRecord userRecord = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth);
        Map<String, Object> existingClaims = userRecord.getCustomClaims();
        if (existingClaims.size() != 0) {
            List<String> existingRoles = new ArrayList<>(existingClaims.keySet());
            throw new UnsupportedOperationException("role %s already exists on user".formatted(existingRoles.get(0)));
        }
        Map<String, Object> modifiedClaims = new HashMap<>();
        existingClaims.forEach(modifiedClaims::put);
        modifiedClaims.put(requestBody.getRole(), true);
        firebaseAuth.setCustomUserClaims(userRecord.getUid(), modifiedClaims);
        return "added role %s successfully".formatted(requestBody.getRole());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + User.ASSOCIATE_ROLE + "', '" + User.MANAGER_ROLE + "')")
    @TrackExecutionTime
    public List<GetUserDto> getUsers() throws Exception {
        ListUsersPage page = firebaseAuth.listUsers(null);
        List<ExportedUserRecord> result = new ArrayList<>();
        page.iterateAll().forEach(result::add);
        return result.stream().map(record -> {
            new ArrayList<>(record.getCustomClaims().keySet());
            List<String> roles = new ArrayList<>(record.getCustomClaims().keySet());
            return GetUserDto.builder().uid(record.getUid()).displayName(record.getDisplayName())
                    .email(record.getEmail()).roles(roles).build();
        }).collect(Collectors.toList());
    }

}
