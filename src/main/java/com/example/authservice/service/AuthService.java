package com.example.authservice.service;

import com.example.authservice.model.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    void register(User user);

    void login(String username, String password);

    void logout(Long userId);
}
