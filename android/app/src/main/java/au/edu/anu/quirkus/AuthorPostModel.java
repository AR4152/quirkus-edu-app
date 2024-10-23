package au.edu.anu.quirkus;

import au.edu.anu.quirkus.data.Post;

public class AuthorPostModel {
    private final Post post;
    private final UserModel author;

    public AuthorPostModel(Post post, UserModel author) {
        this.post = post;
        this.author = author;
    }

    public UserModel getAuthor() {
        return author;
    }

    public Post getPost() {
        return post;
    }
}
