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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class CommentController {

    @Autowired
    private CommentRepo commentRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PostRepo postRepository;

    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> commentMap) {
        // Check if the comment body, user ID, and post ID are missing
        if (!commentMap.containsKey("commentBody") || !commentMap.containsKey("userID") || !commentMap.containsKey("postID")) {
            ErrorResponse errorResponse = new ErrorResponse("Comment body, User ID, or Post ID is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Extract comment body, user ID, and post ID from the map
        String commentBody = (String) commentMap.get("commentBody");
        int userID = Integer.parseInt(commentMap.get("userID").toString());
        int postID = Integer.parseInt(commentMap.get("postID").toString());

        // Check if the user exists in the database
        Optional<User> userOptional = userRepository.findById(userID);
        if (userOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("User does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Fetch commenter's details
        User commenterUser = userOptional.get();

        // Check if the post exists in the database
        Optional<Post> postOptional = postRepository.findById(postID);
        if (postOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Post does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Create a new Comment object and save it
        Comment comment = new Comment(commentBody, commenterUser, postOptional.get());
        comment.setCommenterUserID(userID); // Set commenter's user ID
        comment.setCommenterName(commenterUser.getName()); // Set commenter's name
        commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED).body("Comment created successfully");
    }

    @PatchMapping("/comment")
    public ResponseEntity<?> editComment(@RequestBody Map<String, Object> commentMap) {
        // Check if the comment body and comment ID are missing
        if (!commentMap.containsKey("commentBody") || !commentMap.containsKey("commentID")) {
            ErrorResponse errorResponse = new ErrorResponse("Comment body or Comment ID is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Extract comment body and comment ID from the map
        String commentBody = (String) commentMap.get("commentBody");
        int commentID = Integer.parseInt(commentMap.get("commentID").toString());

        // Check if the comment exists in the database
        Optional<Comment> commentOptional = commentRepository.findById(commentID);
        if (commentOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Comment does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Update the comment body
        Comment comment = commentOptional.get();
        comment.setCommentBody(commentBody);
        commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.OK).body("Comment edited successfully");
    }

    @DeleteMapping("/comment")
    public ResponseEntity<?> deleteComment(@RequestParam int commentID) {
        // Check if the comment exists in the database
        Optional<Comment> commentOptional = commentRepository.findById(commentID);
        if (commentOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Comment does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Delete the comment
        commentRepository.deleteById(commentID);

        return ResponseEntity.status(HttpStatus.OK).body("Comment deleted");
    }

    @GetMapping("/comment")
    public ResponseEntity<?> getCommentById(@RequestParam int commentID) {
        // Check if the comment exists in the database
        Optional<Comment> commentOptional = commentRepository.findById(commentID);
        if (commentOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Comment does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Retrieve the comment
        Comment comment = commentOptional.get();

        // Prepare response with comment details
        Map<String, Object> response = new HashMap<>();
        response.put("commentID", comment.getCommentID());
        response.put("commentBody", comment.getCommentBody());
        response.put("commenterUserID", comment.getCommenterUserID());
        response.put("commenterName", comment.getCommenterName());

        return ResponseEntity.ok(response);
    }

}
