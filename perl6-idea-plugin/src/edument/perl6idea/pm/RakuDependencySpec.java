package edument.perl6idea.pm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakuDependencySpec {
    private final String myName;
    private final Map<String, String> myPairs = new HashMap<>();
    private final Pattern[] LITERAL_PATTERNS = new Pattern[]{Pattern.compile("(\\w+)<([^>]+)>"), // <7.5.1>
        Pattern.compile("(\\w+)\\(([^)]+)\\)"), // (1) or ('1')
        Pattern.compile("(\\d+)(\\w+)")  // :2api
    };

    public RakuDependencySpec(String idString) {
        // the original spec for a name is too complicated, so we have to cheat, as usual caring about
        // the most common cases
        String name;
        String[] parts = idString.split("::");
        if (parts[parts.length - 1].contains(":")) {
            String[] namePieceWithSuffix = parts[parts.length - 1].split(":");
            name = String.join("::", Arrays.copyOf(parts, parts.length - 1));
            if (name.isEmpty())
                name = namePieceWithSuffix[0];
            else
                name += "::" + namePieceWithSuffix[0];
            parseSuffix(Arrays.copyOfRange(namePieceWithSuffix, 1, namePieceWithSuffix.length));
        }
        else {
            name = String.join("::", parts);
        }
        myName = name;
    }

    private void parseSuffix(String[] suffixParts) {
        for (String suffix : suffixParts) {
            for (Pattern pattern : LITERAL_PATTERNS) {
                Matcher matcher = pattern.matcher(suffix);
                if (matcher.matches()) {
                    myPairs.put(matcher.group(1), matcher.group(2));
                    break;
                }
            }
        }
    }

    public String getName() {
        return myName;
    }

    public Map<String, String> getPairs() {
        return myPairs;
    }

    @Override
    public boolean equals(Object obj) {
        // name must be equal
        // check suffixes from obj, if they are present
        if (obj instanceof RakuDependencySpec) {
            if (!((RakuDependencySpec)obj).myName.equals(myName))
                return false;
            Map<String, String> dependencySuffixes = ((RakuDependencySpec)obj).getPairs();
            for (String suffixKey : dependencySuffixes.keySet()) {
                if (!myPairs.containsKey(suffixKey) || myPairs.get(suffixKey).equals(dependencySuffixes.get(suffixKey)))
                    return false;
            }
            return true;
        }
        return super.equals(obj);
    }
}
