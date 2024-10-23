package au.edu.anu.quirkus.data;

import java.util.Date;
import java.util.List;

import au.edu.anu.quirkus.AssessmentResult;

public class PostAssessment extends PostRoot {
    private final AssessmentResult assessmentResult;
    private final String resultReplyId;
    public PostAssessment(String id, String authorId, String title, String body, String tag, String subTag, List<String> keywords, Date created, List<String> upvoters, boolean publicPost, boolean anonymousPost, AssessmentResult assessmentResult, String resultReplyId) {
        super(id, authorId, title, body, tag, subTag, keywords, created, upvoters, publicPost, anonymousPost);

        this.assessmentResult = assessmentResult;
        this.resultReplyId = resultReplyId;
    }

    public AssessmentResult getAssessmentResult() {
        return assessmentResult;
    }

    public String getResultReplyId() {
        return resultReplyId;
    }
}
