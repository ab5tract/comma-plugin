package org.raku.comma.sdk;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuVariableDecl;
import org.raku.comma.psi.RakuRoutineDecl;
import org.raku.comma.psi.external.ExternalRakuPackageDecl;
import org.raku.comma.psi.external.ExternalRakuRoutineDecl;
import org.raku.comma.psi.external.ExternalRakuVariableDecl;
import org.raku.comma.psi.symbols.RakuExplicitSymbol;
import org.raku.comma.psi.symbols.RakuSymbol;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class RakuExternalNamesParser {
    private JSONArray myJson;
    private final Project myProject;
    private final RakuFile myFile;
    private final List<RakuSymbol> result = new ArrayList<>();
    private final Map<String, RakuPackageDecl> externalClasses = new HashMap<>();
    private final Map<String, RakuPackageDecl> metamodelCache = new HashMap<>();

    public RakuExternalNamesParser(Project project, RakuFile file, JSONArray json) {
        myProject = project;
        myFile = file;
        myJson = json;
    }

    public RakuExternalNamesParser(Project project, RakuFile file, String json) {
        myProject = project;
        myFile = file;
        try {
            myJson = new JSONArray(json);
        } catch (JSONException e) {
            Logger.getInstance(RakuExternalNamesParser.class).warn("Tried to parse a JSONArray out of [" + json + "]; for file '" + file.getName() + "'");
            myJson = new JSONArray();
        }
    }

    public RakuExternalNamesParser parse() {
        try {
            for (Object object : myJson) {
                if (!(object instanceof JSONObject j)) continue;

                switch (j.getString("k")) {
                    case "n": {
                        RakuPackageDecl psi = new ExternalRakuPackageDecl(
                            myProject, myFile, "", j.getString("n"), j.getString("t"), "");
                        result.add(new RakuExplicitSymbol(RakuSymbolKind.TypeOrConstant, psi));
                        break;
                    }
                    case "v": {
                        ExternalRakuVariableDecl decl = new ExternalRakuVariableDecl(
                            myProject, myFile, j.getString("n"), "our", j.getString("t"));
                        if (j.has("d"))
                            decl.setDocs(j.getString("d"));
                        result.add(new RakuExplicitSymbol(RakuSymbolKind.Variable, decl));
                        break;
                    }
                    case "m":
                    case "s":
                    case "r": {
                        int isMulti = j.getInt("m");
                        String deprecationMessage = j.has("x") ? j.getString("x") : null;
                        ExternalRakuRoutineDecl psi = new ExternalRakuRoutineDecl(
                            myProject, myFile, j.getString("k"), j.getString("k").equals("m") ? "has" : "our",
                            j.getString("n"), isMulti == 0 ? "only" : "multi",
                            deprecationMessage, j.getJSONObject("s"), j.has("p"));
                        if (j.has("d"))
                            psi.setDocs(j.getString("d"));
                        if (j.has("rakudo"))
                            psi.setImplementationDetail(true);
                        result.add(new RakuExplicitSymbol(RakuSymbolKind.Routine, psi));
                        break;
                    }
                    case "e":
                    case "ss": {
                        ExternalRakuPackageDecl
                          psi = new ExternalRakuPackageDecl(myProject, myFile, "c", j.getString("n"), j.getString("t"), "A");
                        if (j.has("d"))
                            psi.setDocs(j.getString("d"));
                        result.add(new RakuExplicitSymbol(RakuSymbolKind.TypeOrConstant, psi));
                        break;
                    }
                    case "mm": {
                        ExternalRakuPackageDecl psi = parsePackageDeclaration(j, new ArrayList<>());
                        // Add to a metamodel cache to apply to users
                        metamodelCache.put(j.getString("key"), psi);
                        psi.setName(j.getString("key"));
                        externalClasses.put(psi.getName(), psi);
                        result.add(new RakuExplicitSymbol(RakuSymbolKind.TypeOrConstant, psi));
                        break;
                    }
                    case "c":
                    case "ro": {
                        List<String> mro = ContainerUtil.map(j.getJSONArray("mro").toList(), item -> Objects.toString(item, null));
                        ExternalRakuPackageDecl psi = parsePackageDeclaration(j, mro);
                        RakuPackageDecl metamodel = metamodelCache.getOrDefault(psi.getPackageKind(), null);
                        if (metamodel != null)
                            psi.setMetaClass(metamodel);
                        externalClasses.put(psi.getName(), psi);
                        result.add(new RakuExplicitSymbol(RakuSymbolKind.TypeOrConstant, psi));
                        break;
                    }
                }
            }
        } catch (JSONException ex) {
            Logger.getInstance(RakuExternalNamesParser.class).warn(ex);
        }
        return this;
    }

    @NotNull
    private ExternalRakuPackageDecl parsePackageDeclaration(JSONObject j, List<String> mro) {
        ExternalRakuPackageDecl psi = new ExternalRakuPackageDecl(
          myProject, myFile, j.getString("k"),
          j.getString("n"), j.getString("t"), j.getString("b"),
          new ArrayList<>(), new ArrayList<>(), mro, null);
        if (j.has("d"))
            psi.setDocs(j.getString("d"));

        List<RakuRoutineDecl> routines = new ArrayList<>();
        if (j.has("m") && j.get("m") instanceof JSONArray)
            for (Object routine : j.getJSONArray("m"))
                if (routine instanceof JSONObject routineJson) {
                    int isMulti = routineJson.getInt("m");
                    String deprecationMessage = routineJson.has("x") ? routineJson.getString("x") : null;
                    JSONObject signature = routineJson.getJSONObject("s");
                    ExternalRakuRoutineDecl routineDecl = new ExternalRakuRoutineDecl(
                        myProject, psi,
                        routineJson.getString("k"), "has",
                        routineJson.getString("n"), isMulti == 0 ? "only" : "multi",
                        deprecationMessage, signature, routineJson.has("p"));
                    if (routineJson.has("d"))
                        routineDecl.setDocs(routineJson.getString("d"));
                    if (routineJson.has("rakudo"))
                        routineDecl.setImplementationDetail(true);
                    routines.add(routineDecl);
                }

        List<RakuVariableDecl> attrs = new ArrayList<>();
        if (j.has("a"))
            for (Object attribute : j.getJSONArray("a"))
                if (attribute instanceof JSONObject) {
                    ExternalRakuVariableDecl attributeDecl = new ExternalRakuVariableDecl(
                        myProject, psi, ((JSONObject)attribute).getString("n"),
                        "has", ((JSONObject)attribute).getString("t"));
                    if (((JSONObject)attribute).has("d"))
                        attributeDecl.setDocs(((JSONObject)attribute).getString("d"));
                    attrs.add(attributeDecl);
                }

        psi.setRoutines(routines);
        psi.setAttributes(attrs);
        return psi;
    }

    public List<RakuSymbol> result() {
        return result;
    }

    public Map<String, RakuPackageDecl> getPackages() {
        return externalClasses;
    }
}
