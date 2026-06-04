package com.livecoding.arena;

import com.livecoding.arena.entity.User;
import com.livecoding.arena.repository.UserRepository;
import com.livecoding.arena.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        User.Role role = User.Role.STUDENT;

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User(username, email, "encodedPassword", role));

        // Act
        User result = userService.registerUser(username, email, password, role);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameExists() {
        // Arrange
        String username = "existinguser";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.registerUser(username, "test@example.com", "password", User.Role.STUDENT));
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String encodedPassword = "encodedPassword";
        
        User user = new User(username, "test@example.com", encodedPassword, User.Role.STUDENT);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // Act
        Optional<User> result = userService.authenticateUser(username, password);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    void testAuthenticateUser_InvalidPassword() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";
        String encodedPassword = "encodedPassword";
        
        User user = new User(username, "test@example.com", encodedPassword, User.Role.STUDENT);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // Act
        Optional<User> result = userService.authenticateUser(username, password);

        // Assert
        assertFalse(result.isPresent());
    }
}