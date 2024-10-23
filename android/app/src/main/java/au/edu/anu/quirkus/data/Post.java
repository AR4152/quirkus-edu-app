package au.edu.anu.quirkus.data;

import java.util.Date;
import java.util.List;

public abstract class Post {
    private final String id;
    private final String authorId;
    private final String body;
    private final List<String> keywords;
    private final Date created;
    private final List<String> upvoters;
    private final boolean anonymousPost;

    public Post(String id, String authorId, String body, List<String> keywords, Date created, List<String> upvoters, boolean anonymousPost) {
        this.id = id;
        this.authorId = authorId;
        this.body = body;
        this.keywords = keywords == null ? List.of() : keywords;
        this.created = created == null ? new Date() : created;
        this.upvoters = upvoters == null ? List.of() : upvoters;
        this.anonymousPost = anonymousPost;
    }

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getBody() {
        return body;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public Date getCreated() {
        return created;
    }

    public List<String> getUpvoters() {
        return upvoters;
    }

    public long getUpvoteCount() {
        return upvoters.size();
    }
    public boolean isAnonymousPost() {
        return anonymousPost;
    }
}