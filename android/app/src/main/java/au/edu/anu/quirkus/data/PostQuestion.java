package au.edu.anu.quirkus.data;

import java.util.Date;
import java.util.List;

public class PostQuestion extends PostRoot {
    private final String solvedReplyId;
    public PostQuestion(String id, String authorId, String title, String body, String tag, String subTag, List<String> keywords, Date created, List<String> upvoters, boolean publicPost, boolean anonymousPost, String solvedReplyId) {
        super(id, authorId, title, body, tag, subTag, keywords, created, upvoters, publicPost, anonymousPost);

        this.solvedReplyId = solvedReplyId;
    }

    public String getSolvedReplyId() {
        return solvedReplyId;
    }
}
