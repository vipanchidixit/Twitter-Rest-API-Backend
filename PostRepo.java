package com.oop.project.repo;

import com.oop.project.Post;
import com.oop.project.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepo extends JpaRepository<Post, Integer> {
    List<Post> findByUserOrderByPostidDesc(User user);

}
