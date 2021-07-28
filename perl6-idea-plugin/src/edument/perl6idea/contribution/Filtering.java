package edument.perl6idea.contribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class Filtering {
    public static Collection<String> typeMatch(Collection<String> in, String pattern) {
        String[] patternParts = pattern.toLowerCase(Locale.ENGLISH).split("::");
        Collection<String> result = new ArrayList<>();
        tests: for (String test : in) {
            String[] testParts = test.toLowerCase(Locale.ENGLISH).split("::");
            for (String patternPart : patternParts)
                for (String testPart : testParts)
                    if (testPart.contains(patternPart)) {
                        result.add(test);
                        continue tests;
                    }
        }
        return result;
    }

    public static Collection<String> simpleMatch(Collection<String> in, String pattern) {
        Collection<String> result = new ArrayList<>();
        for (String test : in)
            if (test.contains(pattern))
                result.add(test);
        return result;
    }

    public static Collection<String> variableMatch(Collection<String> in, String pattern) {
        Collection<String> result = new ArrayList<>();
        for (String test : in)
            if (test.contains(pattern) || test.replace('.', '!').contains(pattern))
                result.add(test);
        return result;
    }
}
