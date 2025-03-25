package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);

    User saveUser(User user);

    User createNewUser(Long id);

    String getCurrentAdminUsername();
}
