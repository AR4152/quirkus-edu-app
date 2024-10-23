package au.edu.anu.quirkus.data;

import java.util.Date;
import java.util.List;

public class PostRoot extends Post {
    private final String title;
    private final String tag;
    private final String subTag;
    private final boolean publicPost;

    public PostRoot(String id, String authorId, String title, String body, String tag, String subTag, List<String> keywords, Date created, List<String> upvoters, boolean publicPost, boolean anonymousPost) {
        super(id, authorId, body, keywords, created, upvoters, anonymousPost);
        this.title = title;
        this.tag = tag;
        this.subTag = subTag;
        this.publicPost = publicPost;
    }

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }

    public String getSubTag() {
        return subTag;
    }

    public String getPrintableTag() {
        if (subTag == null) {
            return tag;
        }
        return tag + " - " + subTag;
    }

    public boolean isPublicPost() {
        return publicPost;
    }
}