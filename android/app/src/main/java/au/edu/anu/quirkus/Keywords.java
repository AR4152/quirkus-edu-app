package au.edu.anu.quirkus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Keywords {
    public static Set<String> getKeywordsFromString(String str) {
        if (str == null || str.isEmpty()) return Set.of();

        // Get all words and remove all null or zero length splits
        String[] words = str.toLowerCase().split("\\W+");
        return new HashSet<>(Arrays.asList(words));
    }
}
