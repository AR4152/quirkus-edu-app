package au.edu.anu.quirkus.postSearch;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import au.edu.anu.quirkus.PostType;

public class Parser {
    private final Tokenizer tokenizer;

    public Parser(String str) {
        tokenizer = new Tokenizer(str);
        keywords = new ArrayList<>();
    }

    private Optional<String> tag = Optional.empty();
    private Optional<String> subTag = Optional.empty();
    private Optional<PostType> postType = Optional.empty();
    private Optional<TimeRange> timeRange = Optional.empty();
    private ArrayList<String> keywords;

    public Query applyConditionsToQuery(Query query) {
        if (tag.isPresent()) {
            query = query.whereEqualTo("tag", tag.get());
        }
        if (subTag.isPresent()) {
            query = query.whereEqualTo("subTag", subTag.get());
        }
        if (postType.isPresent()) {
            query = query.whereEqualTo("postType", postType.get());
        }
        if (timeRange.isPresent()) {
            // Set the start and end offsets for the query
            TimeRange t = timeRange.get();
            if (t.start != null) {
                Timestamp ti = new Timestamp(Date.from(t.start.atZone(ZoneOffset.systemDefault()).toInstant()));
                query = query.whereLessThanOrEqualTo("created", ti);
            }
            if (t.end != null) {
                Timestamp ti = new Timestamp(Date.from(t.end.atZone(ZoneOffset.systemDefault()).toInstant()));
                query = query.whereGreaterThanOrEqualTo("created", ti);
            }
        }

        if (keywords.size() > 0) {
            query = query.whereArrayContainsAny("keywords", keywords);
        }

        return query;
    }

    public void parse() {
        Token t;
        while ((t = tokenizer.getCurrentToken()) != null) {
            switch (t.getType()) {
                case STR -> {
                    keywords.add(t.getString());
                    tokenizer.next();
                }
                case HASH -> {
                    tokenizer.next();
                    parseHash();
                }
                case AT -> {
                    tokenizer.next();
                    parseAt();
                } case PERCENT -> {
                    tokenizer.next();
                    parsePercent();
                }
                default -> throw new RuntimeException("Unexpected token");
            }
        }
    }

    private void parseHash() {
        if (tag.isPresent()) {
            throw new RuntimeException("Only one tag per query");
        }

        Token _tag = tokenizer.getCurrentToken();

        if (_tag.getType() != Token.Type.STR) {
            throw new RuntimeException("Expected Tag after #");
        }

        tag = Optional.of(_tag.getString());
        tokenizer.next();
        Token sep = tokenizer.getCurrentToken();
        if (sep != null && sep.getType() == Token.Type.HYPEN) {
            tokenizer.next();
            Token sub = tokenizer.getCurrentToken();
            if (sep != null && sub.getType() != Token.Type.STR) {
                throw new RuntimeException("Expected sub tag after hyphen");
            }
            subTag = Optional.of(sub.getString());
            tokenizer.next();
        }
    }

    private void parsePercent() {
        Token t = tokenizer.getCurrentToken();
        if (t.getType() != Token.Type.STR) {
            throw new RuntimeException("Expected Post Type after %");
        }
        if (postType.isPresent()) {
            throw new RuntimeException("Only one postType allowed per query");
        }

        postType = Optional.ofNullable(PostType.createPostType(t.getString().toUpperCase()));
    }


    private void parseAt() {
        Token t = tokenizer.getCurrentToken();
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (t.getType() == Token.Type.NUMBER) {
            start = parseAtNum();
        }

        t = tokenizer.getCurrentToken();

        if (t.getType() != Token.Type.DOTDOT) throw new RuntimeException("Expected '..' between times");
        tokenizer.next();

        t = tokenizer.getCurrentToken();

        if (t.getType() == Token.Type.NUMBER) {
            end = parseAtNum();
        }

        timeRange = Optional.of(new TimeRange(start, end));
    }

    private LocalDateTime parseAtNum() {
        Token t = tokenizer.getCurrentToken();

        LocalDateTime time = LocalDateTime.now();

        int i = t.getNumber();
        tokenizer.next();
        Token sep = tokenizer.getCurrentToken();
        if (sep.getType() == Token.Type.FSLASH || sep.getType() == Token.Type.HYPEN) {
            tokenizer.next();
            Token month = tokenizer.getCurrentToken();
            if (month.getType() != Token.Type.NUMBER) {
                throw new RuntimeException("Expected Month after '/'");
            }
            tokenizer.next();
            time = LocalDateTime.of(time.getYear(), month.getNumber(), i, 0,0,0);
        } else if (sep.getType() == Token.Type.STR) {
            String str = sep.getString();
            assert str.length() == 1;
            switch (str.charAt(0)) {
                case 's' -> time = time.minusSeconds(i);
                case 'm' -> time = time.minusMinutes(i);
                case 'd' -> time = time.minusDays(i);
                case 'w' -> time = time.minusWeeks(i);
                case 'M' -> time = time.minusMonths(i);
                case 'Y' -> time = time.minusYears(i);
            }
            tokenizer.next();
        }
        return time;
    }

    public class TimeRange {
        public LocalDateTime start;
        public LocalDateTime end;

        public TimeRange(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }
    }
}
