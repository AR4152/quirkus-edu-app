package au.edu.anu.quirkus.data;

import java.util.List;
import java.util.Set;

public class TagDefinition {
    private final String name;
    private final String color;
    private final Set<String> subTags;

    public TagDefinition(String name, String color, Set<String> subTags) {
        this.name = name;
        this.color = color;
        this.subTags = subTags;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Set<String> getSubTags() {
        return subTags;
    }
}
