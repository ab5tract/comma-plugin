package edument.perl6idea.contribution;

import java.util.ArrayList;
import java.util.Collection;

class Filtering {
    public static Collection<String> typeMatch(Collection<String> in, String pattern) {
        String[] patternParts = pattern.toLowerCase().split("::");
        Collection<String> result = new ArrayList<>();
        tests: for (String test : in) {
            String[] testParts = test.toLowerCase().split("::");
            for (String patternPart : patternParts)
                for (String testPart : testParts)
                    if (testPart.indexOf(patternPart) >= 0) {
                        result.add(test);
                        continue tests;
                    }
        }
        return result;
    }

    public static Collection<String> simpleMatch(Collection<String> in, String pattern) {
        Collection<String> result = new ArrayList<>();
        for (String test : in)
            if (test.indexOf(pattern) >= 0)
                result.add(test);
        return result;
    }
}
