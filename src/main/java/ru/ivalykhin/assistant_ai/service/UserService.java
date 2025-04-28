package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User saveUser(User user);

    User createNewUser(Long id);

    User createNewUser(Long id, String username, String firstname, String lastname);

    String getCurrentAdminUsername();

    User registerUser(Long userId, String username, String firstname, String lastname);

    User updateUser(User user, String username, String firstname, String lastname);
}
