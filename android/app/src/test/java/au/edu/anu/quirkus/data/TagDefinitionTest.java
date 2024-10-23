package au.edu.anu.quirkus.data;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

public class TagDefinitionTest {
    @Test
    public void testCategory() {
        Set<String> sub = Set.of("Blah");
        TagDefinition c = new TagDefinition("General", "color", sub);

        assertEquals("General", c.getName());
        assertEquals("color", c.getColor());
        assertEquals(sub, c.getSubTags());
    }
}
