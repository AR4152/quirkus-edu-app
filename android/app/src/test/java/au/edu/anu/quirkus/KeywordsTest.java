package au.edu.anu.quirkus;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Set;

public class KeywordsTest {
    @Test
    public void test() {
        Set<String> s = Keywords.getKeywordsFromString("");
        assertEquals(Set.of(), Keywords.getKeywordsFromString(""));

        assertEquals(Set.of("word"), Keywords.getKeywordsFromString("word"));

        assertEquals(Set.of("word", "another", "comp2100"), Keywords.getKeywordsFromString("word another COMP2100"));

        assertEquals(Set.of("word", "thing"), Keywords.getKeywordsFromString("word              thing"));

        assertEquals(Set.of("comp2100"), Keywords.getKeywordsFromString("COMP2100 COMP2100    COMP2100"));
    }
}
