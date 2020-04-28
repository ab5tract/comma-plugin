package edument.perl6idea.grammar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseResultsModel {
    public static class Node {
        private final String name;
        private final int start;
        private final int end;
        private final List<Node> children;

        public Node(String name, int start, int end, List<Node> children) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.children = children;
        }

        public boolean isSuccessful() {
            return end > 0;
        }

        public String getName() {
            return name;
        }

        public int getStart() {
            return start;
        }

        public List<Node> getChildren() {
            return children;
        }
    }

    private final Node top;
    private final String error;

    public ParseResultsModel(String json) {
        JSONObject wrapper = new JSONObject(json);
        top = wrapper.has("t") ? nodeFromJSON(wrapper.getJSONObject("t")) : null;
        error = wrapper.has("e") ? wrapper.getString("e") : null;
    }

    private Node nodeFromJSON(JSONObject nodeJson) {
        String name = nodeJson.getString("n");
        int start = nodeJson.getInt("s");
        int end = nodeJson.has("p") && nodeJson.getBoolean("p") ? nodeJson.getInt("e") : -1;
        JSONArray childrenJson = nodeJson.getJSONArray("c");
        List<Node> children = new ArrayList<>(childrenJson.length());
        for (Object obj : childrenJson)
            if (obj instanceof JSONObject)
                children.add(nodeFromJSON((JSONObject)obj));
        return new Node(name, start, end, children);
    }

    public Node getTop() {
        return top;
    }

    public String getError() {
        return error;
    }
}
