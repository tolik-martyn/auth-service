package com.example.authservice.service;

import com.example.authservice.model.Session;
import com.example.authservice.model.User;
import com.example.authservice.repository.SessionRepository;
import com.example.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public void register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("User with username " + user.getUsername() + " already exists");
        }
        userRepository.save(user);
    }

    public void login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            Session session = sessionRepository.findByUserId(user.getId());
            if (session == null) {
                session = new Session();
                session.setUserId(user.getId());
            }
            session.setToken(generateToken());
            sessionRepository.save(session);
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public void logout(Long userId) {
        sessionRepository.deleteByUserId(userId);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}