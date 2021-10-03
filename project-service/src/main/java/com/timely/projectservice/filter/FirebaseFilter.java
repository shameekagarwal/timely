package com.timely.projectservice.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.timely.projectservice.constant.ProfilesConstants;
import com.timely.projectservice.model.User;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile({ ProfilesConstants.PROD, ProfilesConstants.DEV, ProfilesConstants.QA })
public class FirebaseFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;

    public static final String HEADER_STRING = "Authorization";
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            UserRecord userRecord = extractUserRecordFromRequest(request, firebaseAuth);
            UserDetails userDetails = new User(userRecord);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    "", userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
        }

        chain.doFilter(request, response);

    }

    public static UserRecord extractUserRecordFromRequest(HttpServletRequest request, FirebaseAuth firebaseAuth)
            throws Exception {

        String header = request.getHeader(HEADER_STRING);
        String token = header.replace(AUTHORIZATION_TOKEN_PREFIX, "");
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        return firebaseAuth.getUser(decodedToken.getUid());

    }

}