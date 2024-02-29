package com.example.authservice.service;

import com.example.authservice.model.Session;
import com.example.authservice.model.User;
import com.example.authservice.repository.SessionRepository;
import com.example.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("AuthService Tests")
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register Successful")
    public void testRegisterSuccessful() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);

        authService.register(user);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(user.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    @DisplayName("Login Successful")
    public void testLoginSuccessful() {
        String username = "testUser";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(sessionRepository.findByUserId(user.getId())).thenReturn(null);

        authService.login(username, password);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(sessionRepository, Mockito.times(1)).findByUserId(user.getId());
        Mockito.verify(sessionRepository, Mockito.times(1)).save(Mockito.any(Session.class));
    }

    @Test
    @DisplayName("Logout Successful")
    public void testLogoutSuccessful() {
        Long userId = 1L;

        authService.logout(userId);

        Mockito.verify(sessionRepository, Mockito.times(1)).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Register User Already Exists")
    public void testRegisterUserAlreadyExists() {
        User user = new User();
        user.setUsername("existingUser");
        user.setPassword("password");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        assertThrows(RuntimeException.class, () -> authService.register(user));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(user.getUsername());
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    @DisplayName("Login Invalid Username")
    public void testLoginInvalidUsername() {
        String username = "nonExistingUser";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authService.login(username, password));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(sessionRepository, Mockito.never()).findByUserId(Mockito.anyLong());
        Mockito.verify(sessionRepository, Mockito.never()).save(Mockito.any(Session.class));
    }

    @Test
    @DisplayName("Login Invalid Password")
    public void testLoginInvalidPassword() {
        String username = "existingUser";
        String password = "incorrectPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword("correctPassword");

        when(userRepository.findByUsername(username)).thenReturn(user);

        assertThrows(RuntimeException.class, () -> authService.login(username, password));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(sessionRepository, Mockito.never()).findByUserId(Mockito.anyLong());
        Mockito.verify(sessionRepository, Mockito.never()).save(Mockito.any(Session.class));
    }
}