package com.oop.project.repo;

import com.oop.project.Comment;
import com.oop.project.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Integer> {
   List<Comment> findByPost(Post post);
}
