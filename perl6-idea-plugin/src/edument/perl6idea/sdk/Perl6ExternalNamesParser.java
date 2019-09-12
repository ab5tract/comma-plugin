package edument.perl6idea.sdk;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.external.ExternalPerl6PackageDecl;
import edument.perl6idea.psi.external.ExternalPerl6RoutineDecl;
import edument.perl6idea.psi.external.ExternalPerl6VariableDecl;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Perl6ExternalNamesParser {
    private final String myJson;
    private final Project myProject;
    private final Perl6File myFile;
    private List<Perl6Symbol> result = new ArrayList<>();
    private Set<String> seen = new HashSet<>();
    private Map<String, Perl6PackageDecl> externalClasses = new HashMap<>();

    public Perl6ExternalNamesParser(Project project, Perl6File file, String json) {
        myProject = project;
        myFile = file;
        myJson = json;
    }

    public Perl6ExternalNamesParser parse() {
        try {
            if (myJson.length() < 2) return this;
            JSONArray file = new JSONArray(myJson);
            for (Object object : file) {
                if (!(object instanceof JSONObject)) continue;

                JSONObject j = (JSONObject)object;

                switch (j.getString("k")) {
                    case "n": {
                        Perl6PackageDecl psi = new ExternalPerl6PackageDecl(
                            myProject, myFile, "", j.getString("n"), j.getString("t"));
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, psi));
                        break;
                    }
                    case "v": {
                        Perl6VariableDecl psi = createVariable(j);
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, psi));
                        break;
                    }
                    case "m":
                    case "s":
                    case "r": {
                        int isMulti = j.getInt("m");
                        Perl6RoutineDecl psi = new ExternalPerl6RoutineDecl(
                            myProject, myFile, j.getString("k"), j.getString("k").equals("m") ? "has" : "our",
                            j.getString("n"), isMulti == 0 ? "only" : isMulti == 1 ? "proto" : "multi", j.getJSONObject("s").getString("r"));
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, psi));
                        break;
                    }
                    case "e": {
                        Perl6PackageDecl psi = new ExternalPerl6PackageDecl(myProject, myFile, "class", j.getString("n"), j.getString("t"));
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, psi));
                        break;
                    }
                    case "ss": {
                        // TODO subsets
                        break;
                    }
                    case "c":
                    case "ro": {
                        List<Perl6RoutineDecl> routines = new ArrayList<>();
                        if (j.has("m"))
                            for (Object routine : j.getJSONArray("m").toList())
                                if (routine instanceof Map) {
                                    Map routineData = (Map)routine;
                                    Integer isMulti = (Integer)routineData.get("m");
                                    routines.add(new ExternalPerl6RoutineDecl(
                                        myProject, myFile,
                                        (String)routineData.get("k"), "has",
                                        (String)routineData.get("n"), isMulti == 0 ? "only" : isMulti == 1 ? "proto" : "multi", "Any"));
                                }

                        List<Perl6VariableDecl> attrs = new ArrayList<>();
                        if (j.has("a"))
                            for (Object attribute : j.getJSONArray("a").toList())
                                if (attribute instanceof Map) {
                                    Map attributeData = (Map)attribute;
                                    attrs.add(new ExternalPerl6VariableDecl(
                                        myProject, myFile, (String)attributeData.get("n"),
                                        "has", (String)attributeData.get("t")));
                                }

                        Perl6PackageDecl psi = new ExternalPerl6PackageDecl(
                            myProject, myFile, j.getString("k"),
                            j.getString("n"), j.getString("t"),
                            routines, attrs);
                        externalClasses.put(psi.getName(), psi);
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, psi));
                        break;
                    }
                }
            }
        } catch (JSONException ex) {
            Logger.getInstance(Perl6ExternalNamesParser.class).warn(ex);
        }
        return this;
    }

    @NotNull
    private Perl6VariableDecl createVariable(JSONObject j) {
        return new ExternalPerl6VariableDecl(
            myProject, myFile, j.getString("n"), "our", j.getString("t"));
    }

    public List<Perl6Symbol> result() {
        return result;
    }

    public Map<String, Perl6PackageDecl> getPackages() {
        return externalClasses;
    }
}
