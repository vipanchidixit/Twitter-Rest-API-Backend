package com.oop.project.controller;

import com.oop.project.Comment;
import com.oop.project.Post;
import com.oop.project.User;
import com.oop.project.repo.PostRepo;
import com.oop.project.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PostController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PostRepo postRepository;

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> postMap) {
        // Check if the post body or user ID is missing
        if (!postMap.containsKey("postBody") || !postMap.containsKey("userID")) {
            ErrorResponse errorResponse = new ErrorResponse("Post body or User ID is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Extract post body and user ID from the map
        String postBody = (String) postMap.get("postBody");
        int userID = Integer.parseInt(postMap.get("userID").toString());

        // Check if the user exists in the database
        Optional<User> userOptional = userRepository.findById(userID);
        if (userOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("User does not exist");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        // Create a new Post object and set the current date
        Post post = new Post(postBody, userOptional.get());
        post.setDate(new Date()); // Set current date

        postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post created successfully");
    }

    @PatchMapping("/post")
    public ResponseEntity<?> editPost(@RequestBody Map<String, Object> requestBody) {
        // Check if the post body and post ID are missing
        if (!requestBody.containsKey("postID") || !requestBody.containsKey("postBody")) {
            ErrorResponse errorResponse = new ErrorResponse("Post ID or body is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Extract post ID and post body from the map
        int postID = (int) requestBody.get("postID");
        String postBody = (String) requestBody.get("postBody");

        // Check if the post exists in the database
        Optional<Post> optionalPost = postRepository.findById(postID);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setPostBody(postBody);
            postRepository.save(post);
            return ResponseEntity.ok("Post edited successfully");
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Post does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


    @DeleteMapping("/post")
    public ResponseEntity<?> deletePost(@RequestParam int postID) {
        Optional<Post> optionalPost = postRepository.findById(postID);
        if (optionalPost.isPresent()) {
            postRepository.delete(optionalPost.get());
            return ResponseEntity.ok("Post deleted");
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Post does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPostWithComments(@RequestParam int postID) {
        Optional<Post> postOptional = postRepository.findById(postID);
        if (postOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        Post post = postOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("postID", post.getPostID());
        response.put("postBody", post.getPostbody());
        response.put("date", post.getDate());

        List<Comment> comments = post.getComments();
        if (!comments.isEmpty()) {
            response.put("comments", getCommentsInfo(comments));
        }

        return ResponseEntity.ok(response);
    }

    private List<Map<String, Object>> getCommentsInfo(List<Comment> comments) {
        List<Map<String, Object>> commentsInfo = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> commentInfo = new HashMap<>();
            commentInfo.put("commentID", comment.getCommentID());
            commentInfo.put("commentBody", comment.getCommentBody());
            commentInfo.put("commenterUserID", comment.getCommenterUserID());
            commentInfo.put("commenterName", comment.getCommenterName());
            commentsInfo.add(commentInfo);
        }
        return commentsInfo;
    }
}
