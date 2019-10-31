package edument.perl6idea.sdk;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.external.ExternalPerl6PackageDecl;
import edument.perl6idea.psi.external.ExternalPerl6RoutineDecl;
import edument.perl6idea.psi.external.ExternalPerl6VariableDecl;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Perl6ExternalNamesParser {
    private JSONArray myJson;
    private final Project myProject;
    private final Perl6File myFile;
    private List<Perl6Symbol> result = new ArrayList<>();
    private Map<String, Perl6PackageDecl> externalClasses = new HashMap<>();

    public Perl6ExternalNamesParser(Project project, Perl6File file, JSONArray json) {
        myProject = project;
        myFile = file;
        myJson = json;
    }

    public Perl6ExternalNamesParser(Project project, Perl6File file, String json) {
        myProject = project;
        myFile = file;
        try {
            myJson = new JSONArray(json);
        } catch (JSONException e) {
            Logger.getInstance(Perl6ExternalNamesParser.class).warn(e);
            myJson = new JSONArray();
        }
    }

    public Perl6ExternalNamesParser parse() {
        try {
            for (Object object : myJson) {
                if (!(object instanceof JSONObject)) continue;

                JSONObject j = (JSONObject)object;

                switch (j.getString("k")) {
                    case "n": {
                        Perl6PackageDecl psi = new ExternalPerl6PackageDecl(
                            myProject, myFile, "", j.getString("n"), j.getString("t"), "");
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, psi));
                        break;
                    }
                    case "v": {
                        ExternalPerl6VariableDecl decl = new ExternalPerl6VariableDecl(
                            myProject, myFile, j.getString("n"), "our", j.getString("t"));
                        if (j.has("d"))
                            decl.setDocs(j.getString("d"));
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, decl));
                        break;
                    }
                    case "m":
                    case "s":
                    case "r": {
                        int isMulti = j.getInt("m");
                        ExternalPerl6RoutineDecl psi = new ExternalPerl6RoutineDecl(
                            myProject, myFile, j.getString("k"), j.getString("k").equals("m") ? "has" : "our",
                            j.getString("n"), isMulti == 0 ? "only" : "multi", j.getJSONObject("s"));
                        if (j.has("d"))
                            psi.setDocs(j.getString("d"));
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, psi));
                        break;
                    }
                    case "e":
                    case "ss": {
                        ExternalPerl6PackageDecl psi = new ExternalPerl6PackageDecl(myProject, myFile, "c", j.getString("n"), j.getString("t"), "A");
                        if (j.has("d"))
                            psi.setDocs(j.getString("d"));
                        result.add(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, psi));
                        break;
                    }
                    case "c":
                    case "ro": {
                        List<Perl6RoutineDecl> routines = new ArrayList<>();
                        if (j.has("m"))
                            for (Object routine : j.getJSONArray("m"))
                                if (routine instanceof JSONObject) {
                                    int isMulti = ((JSONObject)routine).getInt("m");
                                    JSONObject signature = ((JSONObject)routine).getJSONObject("s");
                                    ExternalPerl6RoutineDecl routineDecl = new ExternalPerl6RoutineDecl(
                                        myProject, myFile,
                                        ((JSONObject)routine).getString("k"), "has",
                                        ((JSONObject)routine).getString("n"), isMulti == 0 ? "only" : "multi",
                                        signature);
                                    if (((JSONObject)routine).has("d"))
                                        routineDecl.setDocs(((JSONObject)routine).getString("d"));
                                    routines.add(routineDecl);
                                }

                        List<Perl6VariableDecl> attrs = new ArrayList<>();
                        if (j.has("a"))
                            for (Object attribute : j.getJSONArray("a"))
                                if (attribute instanceof JSONObject) {
                                    ExternalPerl6VariableDecl attributeDecl = new ExternalPerl6VariableDecl(
                                        myProject, myFile, ((JSONObject)attribute).getString("n"),
                                        "has", ((JSONObject)attribute).getString("t"));
                                    if (((JSONObject)attribute).has("d"))
                                        attributeDecl.setDocs(((JSONObject)attribute).getString("d"));
                                    attrs.add(attributeDecl);
                                }

                        List<String> mro = ContainerUtil.map(j.getJSONArray("mro").toList(), item -> Objects.toString(item, null));
                        ExternalPerl6PackageDecl psi = new ExternalPerl6PackageDecl(
                            myProject, myFile, j.getString("k"),
                            j.getString("n"), j.getString("t"), j.getString("b"),
                            routines, attrs, mro);
                        if (j.has("d"))
                            psi.setDocs(j.getString("d"));
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

    public List<Perl6Symbol> result() {
        return result;
    }

    public Map<String, Perl6PackageDecl> getPackages() {
        return externalClasses;
    }
}
