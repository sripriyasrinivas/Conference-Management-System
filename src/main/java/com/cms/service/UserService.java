package com.cms.service;

import com.cms.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findByRole(User.Role role);
    User updateUser(User user);
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAll();
    User getCurrentUser();
}
