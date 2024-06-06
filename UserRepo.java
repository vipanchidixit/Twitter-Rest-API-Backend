package com.oop.project.repo;

import com.oop.project.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository< User, Integer> {
    User findByEmail(String email);

}
