package ru.ivalykhin.assistant_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ivalykhin.assistant_ai.model.User;
import ru.ivalykhin.assistant_ai.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User createNewUser(Long id) {
        User user = User.builder().id(id).build();
        return userRepository.save(user);
    }

    public User createNewUser(Long id, String username, String firstname, String lastname) {
        User user = User.builder()
                .id(id)
                .username(username)
                .firstName(firstname)
                .lastName(lastname)
                .build();
        return userRepository.save(user);
    }

    public User updateUser(User user, String username, String firstname, String lastname) {
        if (!Objects.equals(username, user.getUsername())) {
            user.setUsername(username);
        }
        if (!Objects.equals(firstname, user.getFirstName())) {
            user.setFirstName(firstname);
        }
        if (!Objects.equals(lastname, user.getLastName())) {
            user.setLastName(lastname);
        }
        return userRepository.save(user);
    }

    public String getCurrentAdminUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "Аноним"; // Если пользователь не аутентифицирован
    }

    public User registerUser(Long userId, String username, String firstname, String lastname) {
        Optional<User> userResult = getUserById(userId);
        User user;
        if (userResult.isPresent()) {
            user = updateUser(userResult.get(), username, firstname, lastname);
        } else {
            user = createNewUser(userId, username, firstname, lastname);
        }
        return user;
    }
}
