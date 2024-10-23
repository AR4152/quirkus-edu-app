package au.edu.anu.quirkus.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course {
    private final String id;
    private final String name;
    private final String description;
    private final boolean archived;
    private final Map<String, TagDefinition> tagDefinitions;

    public Course(String id, String name, String description, Map<String, TagDefinition> tagDefinitions, boolean archived) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.archived = archived;
        this.tagDefinitions = tagDefinitions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isArchived() {
        return archived;
    }

    public Map<String, TagDefinition> getTagDefinitions() {
        return tagDefinitions;
    }
}
