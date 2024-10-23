package au.edu.anu.quirkus.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CourseTest {

    @Test
    public void testCourse() {
        Course c = new Course("id", "COMP2100", "desc", Map.of(), false);

        assertEquals("id", c.getId());
        assertEquals("COMP2100", c.getName());
        assertEquals("desc", c.getDescription());
        assertEquals(Map.of(), c.getTagDefinitions());
        assertEquals(false, c.isArchived());
    }
}
