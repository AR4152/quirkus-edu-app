package au.edu.anu.quirkus.data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class PostReply extends Post {
    private final String replyToId;

    public PostReply(String id, String authorId, String replyToId, String body, List<String> keywords, Date created, List<String> upvoters, boolean anonymousPost) {
        super(id, authorId, body, keywords, created, upvoters, anonymousPost);
        this.replyToId = replyToId;
    }

    public String getReplyToId() {
        return replyToId;
    }
}