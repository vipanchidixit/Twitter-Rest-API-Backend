package com.oop.project.controller;

import com.oop.project.Comment;
import com.oop.project.Post;
import com.oop.project.User;
import com.oop.project.repo.CommentRepo;
import com.oop.project.repo.PostRepo;
import com.oop.project.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserControl {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private CommentRepo commentRepo;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody User user) {
        // Check if the user already exists
        User existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Forbidden, Account already exists"));
        }

        // Save the new user
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Account Creation successful");
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        User existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser != null) {
            if (existingUser.getPassword().equals(user.getPassword())) {
                return ResponseEntity.ok("Login Successful");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Username/Password Incorrect"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("User does not exist"));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetail(@RequestParam int userID) {
        Optional<User> userData = userRepo.findById(userID);
        if (userData.isPresent()) {
            User user = userData.get();
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("userID", user.getUserID());
            userDetails.put("name", user.getName());
            userDetails.put("email", user.getEmail());
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User does not exist"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> allUsers() {
        List<User> users = userRepo.findAll();
        if (!users.isEmpty()) {
            List<Map<String, Object>> usersList = new ArrayList<>();
            for (User user : users) {
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("userID", user.getUserID());
                userDetails.put("name", user.getName());
                userDetails.put("email", user.getEmail());
                usersList.add(userDetails);
            }
            return ResponseEntity.ok(usersList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("No users found"));
        }
    }

    @Autowired
    private PostRepo postRepository; // Assuming you have a repository for Post
    @Autowired
    private CommentRepo commentRepository; // Assuming you have a repository for Comment

    @GetMapping("/")
    public ResponseEntity<?> getUserFeed() {
        List<User> users = userRepo.findAll();

        List<Map<String, Object>> userFeed = new ArrayList<>();

        users.sort(Comparator.comparingInt(User::getUserID).reversed());

        for (User user : users) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("user", user.getName());

            List<Post> posts = postRepo.findByUserOrderByPostidDesc(user);

            List<Map<String, Object>> userPosts = new ArrayList<>();

            for (Post post : posts) {
                Map<String, Object> postData = new HashMap<>();
                postData.put("postID", post.getPostID());
                postData.put("postBody", post.getPostbody());
                postData.put("date", post.getDate());

                List<Comment> comments = commentRepo.findByPost(post);

                List<Map<String, Object>> postComments = new ArrayList<>();

                for (Comment comment : comments) {
                    Map<String, Object> commentData = new HashMap<>();
                    commentData.put("commenterName", comment.getCommenterName());
                    commentData.put("commenterUserID", comment.getCommenterUserID());
                    commentData.put("commentBody", comment.getCommentBody());
                    postComments.add(commentData);
                }

                postData.put("comments", postComments);
                userPosts.add(postData);
            }

            userData.put("posts", userPosts);
            userFeed.add(userData);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userFeed);
    }
}
