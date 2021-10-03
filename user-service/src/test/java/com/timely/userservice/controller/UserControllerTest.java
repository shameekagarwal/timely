package com.timely.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.timely.userservice.dto.AddRoleRequestDto;
import com.timely.userservice.filter.FirebaseFilter;
import com.timely.userservice.model.User;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private FirebaseAuth firebaseAuth;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void whenPing_thenSuccess() throws Exception {
        mvc.perform(get("/ping")).andExpect(status().isOk()).andExpect(content().string("pong from user-service"));
    }

    @Test
    public void whenAddRole_thenSuccess() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {

            String userUid = RandomStringUtils.randomAlphabetic(15);
            UserRecord userRecord = mock(UserRecord.class);
            when(userRecord.getCustomClaims()).thenReturn(new HashMap<>());
            when(userRecord.getUid()).thenReturn(userUid);
            String roleToAdd = User.ASSOCIATE_ROLE;
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecord);
            AddRoleRequestDto requestBody = new AddRoleRequestDto();
            requestBody.setRole(roleToAdd);

            mvc.perform(put("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("added role %s successfully".formatted(roleToAdd)));
            Map<String, Object> expectedClaims = new HashMap<>();
            expectedClaims.put(roleToAdd, true);
            verify(firebaseAuth, times(1)).setCustomUserClaims(userUid, expectedClaims);

        }
    }

    @Test
    public void whenAddRole_thenFailure() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {

            String userUid = RandomStringUtils.randomAlphabetic(15);
            UserRecord userRecord = mock(UserRecord.class);
            String existingRole = User.MANAGER_ROLE;
            Map<String, Object> claims = new HashMap<>();
            claims.put(existingRole, true);
            when(userRecord.getCustomClaims()).thenReturn(claims);
            when(userRecord.getUid()).thenReturn(userUid);
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecord);
            String roleToAdd = User.ASSOCIATE_ROLE;
            AddRoleRequestDto requestBody = new AddRoleRequestDto();
            requestBody.setRole(roleToAdd);

            mvc.perform(put("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(requestBody)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.metaInfo").value("role %s already exists on user".formatted(existingRole)));

        }
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

}
