package org.raku.comma.grammar;

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
        private final List<String> captureNames;

        public Node(String name, int start, int end, List<Node> children, List<String> captureNames) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.children = children;
            this.captureNames = captureNames;
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

        public int getEnd() {
            return end;
        }

        public List<Node> getChildren() {
            return children;
        }

        public List<String> getCaptureNames() {
            return captureNames;
        }

        public int findFurthestMatchPoint() {
            int furthest = end;
            for (Node child : children) {
                int childFurthest = child.findFurthestMatchPoint();
                if (childFurthest > furthest)
                    furthest = childFurthest;
            }
            return furthest;
        }

        public int findEndOfMostSuccessfulChild() {
            int end = start;
            for (Node child : children)
                if (child.isSuccessful() && child.getEnd() > end)
                        end = child.getEnd();
            return end;
        }

        public boolean isProtoCandidate() {
            return name.contains(":");
        }
    }

    public static class FailurePositions {
        private final int highwaterStart;
        private final int failureStart;
        private final int failureEnd;

        public FailurePositions(int highwaterStart, int failureStart, int failureEnd) {
            this.highwaterStart = highwaterStart;
            this.failureStart = failureStart;
            this.failureEnd = failureEnd;
        }

        public int getHighwaterStart() {
            return highwaterStart;
        }

        public int getFailureStart() {
            return failureStart;
        }

        public int getFailureEnd() {
            return failureEnd;
        }
    }

    private final String input;
    private final Node top;
    private final String error;

    public ParseResultsModel(String input, String json) {
        this.input = input;
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

        List<String> captureNames;
        if (nodeJson.has("x")) {
            JSONArray captureNamesJson = nodeJson.getJSONArray("x");
            captureNames = new ArrayList<>(captureNamesJson.length());
            for (Object captureName : captureNamesJson)
                if (captureName instanceof String)
                    captureNames.add((String)captureName);
        }
        else {
            captureNames = null;
        }

        return new Node(name, start, end, children, captureNames);
    }

    public Node getTop() {
        return top;
    }

    public String getError() {
        return error;
    }

    public FailurePositions getFailurePositions() {
        // If we parsed the whole thing, no error.
        if (top == null)
            return null;
        if (top.isSuccessful() && top.getEnd() == input.length())
            return null;

        // Start of the highwater depends on if we matched or not (if TOP did
        // then we just didn't eat what comes next; otherwise, we look through
        // the children of top to find the furthest that it got).
        int startHighwater = top.isSuccessful()
                             ? top.getEnd()
                             : top.findEndOfMostSuccessfulChild();

        // Otherwise, find the furthest point we ever matched, which is our
        // highwater mark.
        int endHighwater = top.findFurthestMatchPoint();

        // Finally, end of error is the length of the input.
        return new FailurePositions(startHighwater, endHighwater, input.length());
    }
}
