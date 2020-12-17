package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.type.Perl6Type;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalPerl6Signature extends Perl6ExternalPsiElement implements Perl6Signature {
    private Perl6Parameter[] myParameters;

    public ExternalPerl6Signature(Project project, PsiElement parent, JSONObject signature) {
        myProject = project;
        myParent = parent;
        JSONArray paramsJSON = signature.getJSONArray("p");
        List<ExternalPerl6Parameter> params = new ArrayList<>();
        for (Object param : paramsJSON) {
            if (param instanceof JSONObject) {
                params.add(new ExternalPerl6Parameter(project, parent,
                        ((JSONObject) param).getString("n"),
                        ((JSONObject) param).getString("t")));
            }
        }
        myParameters = params.toArray(new ExternalPerl6Parameter[0]);
    }

    @Override
    public String summary(Perl6Type retType) {
        return String.join(", ", Arrays.stream(myParameters).map(p -> p.summary()).toArray(String[]::new)) + " --> " + retType.getName();
    }

    @Override
    public Perl6Parameter[] getParameters() {
        return myParameters;
    }
}
