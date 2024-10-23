package au.edu.anu.quirkus.ui.thread;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import au.edu.anu.quirkus.data.Post;
import au.edu.anu.quirkus.data.PostRoot;
import au.edu.anu.quirkus.data.PostReply;
import au.edu.anu.quirkus.data.PostsReplyRepository;
import au.edu.anu.quirkus.data.PostsRepository;

public class ThreadViewModel extends ViewModel {
    private PostsReplyRepository repliesRepository;
    private PostsRepository postRepository;
    private LiveData<Post> post;
    private LiveData<List<Post>> replies;

    public ThreadViewModel() {
        repliesRepository = new PostsReplyRepository();
        postRepository = new PostsRepository();
    }

    public void setPostID(String courseID, String postID) {
        post = postRepository.getPostByID(courseID, postID);
        replies = repliesRepository.getReplies(courseID, postID);
    }

    public LiveData<Post> getPost() {
        return post;
    }

    public LiveData<List<Post>> getReplies() {
        return replies;
    }

}
