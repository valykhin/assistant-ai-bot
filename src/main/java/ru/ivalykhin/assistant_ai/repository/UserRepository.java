package ru.ivalykhin.assistant_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ivalykhin.assistant_ai.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
