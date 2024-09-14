package org.raku.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.raku.psi.RakuParameter;
import org.raku.psi.RakuSignature;
import org.raku.psi.type.RakuType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalRakuSignature extends RakuExternalPsiElement implements RakuSignature {
    private final RakuParameter[] myParameters;

    public ExternalRakuSignature(Project project, PsiElement parent, JSONObject signature) {
        myProject = project;
        myParent = parent;
        JSONArray paramsJSON = signature.getJSONArray("p");
        List<ExternalRakuParameter> params = new ArrayList<>();
        for (Object param : paramsJSON) {
            if (param instanceof JSONObject) {
                params.add(new ExternalRakuParameter(project, parent,
                                                     ((JSONObject) param).getString("n"),
                                                      ((JSONObject)param).has("nn") ? ((JSONObject) param).getJSONArray("nn").toList() : null,
                                                     ((JSONObject) param).getString("t")));
            }
        }
        myParameters = params.toArray(new ExternalRakuParameter[0]);
    }

    @Override
    public String summary(RakuType retType) {
        return String.join(", ", Arrays.stream(myParameters).map(p -> p.summary(false)).toArray(String[]::new)) + " --> " + retType.getName();
    }

    @Override
    public RakuParameter[] getParameters() {
        return myParameters;
    }
}
