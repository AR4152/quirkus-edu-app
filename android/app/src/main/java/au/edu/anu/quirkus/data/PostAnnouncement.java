package au.edu.anu.quirkus.data;

import java.util.Date;
import java.util.List;

public class PostAnnouncement extends PostRoot {
    public PostAnnouncement(String id, String authorId, String title, String body, String tag, String subTag, List<String> keywords, Date created, List<String> upvoters, boolean publicPost, boolean anonymousPost) {
        super(id, authorId, title, body, tag, subTag, keywords, created, upvoters, publicPost, anonymousPost);
    }
}
