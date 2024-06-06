package com.oop.project;
import jakarta.persistence.*;


@Entity
@Table(name = "Comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentID;

    @Column(nullable = false)
    private String commentBody;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User commentor;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "commenter_user_id")
    private int commenterUserID; // commenter's user ID

    @Column(name = "commenter_name")
    private String commenterName; // commenter's name

    // Constructors
    public Comment() {
        // Default constructor
    }

    public Comment(String commentBody, User commentor, Post post) {
        this.commentBody = commentBody;
        this.commentor = commentor;
        this.post = post;
    }

    // Getters and Setters
    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public User getCommentor() {
        return commentor;
    }

    public void setCommentor(User commentor) {
        this.commentor = commentor;
    }

    public int getCommenterUserID() {
        return commenterUserID;
    }

    public void setCommenterUserID(int commenterUserID) {
        this.commenterUserID = commenterUserID;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}

