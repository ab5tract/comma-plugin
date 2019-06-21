package edument.perl6idea.cro;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.contribution.Filtering;
import edument.perl6idea.extensions.Perl6FrameworkCall;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.impl.Perl6SubCallImpl;
import edument.perl6idea.psi.stub.Perl6SubCallStub;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class CroFrameworkCall extends Perl6FrameworkCall {
    private static final Set<String> ROUTE_VERBS;

    static {
        ROUTE_VERBS = new HashSet<>();
        ROUTE_VERBS.addAll(Arrays.asList("get", "put", "post", "delete", "patch"));
    }

    @Override
    public String getFrameworkName() {
        return "Cro Router";
    }

    @Override
    public boolean isApplicable(Perl6SubCall call) {
        // We can't resolve the symbol at indexing time to check if it's coming from
        // the Cro HTTP router, so we just go on callee name and it having a single
        // sub argument for now.
        String calleeName = call.getCalleeName();
        return ROUTE_VERBS.contains(calleeName) && getRouteSignature(call) != null;
    }

    private Perl6Parameter[] getRouteSignature(Perl6SubCall call) {
        Perl6PointyBlock pblock = PsiTreeUtil.getChildOfType(call, Perl6PointyBlock.class);
        if (pblock != null)
            return pblock.getParams();
        Perl6RoutineDecl routine = PsiTreeUtil.getChildOfType(call, Perl6RoutineDecl.class);
        if (routine != null)
            return routine.getParams();
        return null;
    }

    @Override
    public Map<String, String> getFrameworkData(Perl6SubCall call) {
        StringBuilder buffer = new StringBuilder();
        Perl6Parameter[] params = getRouteSignature(call);
        Map<String, String> result = new HashMap<>();
        if (params != null) {
            for (Perl6Parameter param : params)
                renderParameter(buffer, param);
            result.put("method", call.getCalleeName());
            result.put("path", buffer.length() == 0 ? "/" : buffer.toString());
        }
        return result;
    }

    private void renderParameter(StringBuilder buffer, Perl6Parameter param) {
        // We'll only deal with positional parameters, which are part of the
        // route path.
        if (param.isPositional()) {
            buffer.append("/");
            String varName = param.getVariableName();
            boolean haveVarName = varName != null && !varName.isEmpty();
            if (param.isSlurpy()) {
                buffer.append("{");
                if (haveVarName)
                    buffer.append(varName);
                buffer.append("*}");
            }
            else if (haveVarName) {
                buffer.append("{");
                buffer.append(varName);
                if (param.isOptional())
                    buffer.append("?");
                buffer.append("}");
            }
            else {
                Perl6PsiElement value = param.getValueConstraint();
                if (value instanceof Perl6StrLiteral) {
                    buffer.append(((Perl6StrLiteral)value).getStringText());
                }
                else {
                    buffer.append("<unknown>");
                }
            }
        }
    }

    @Override
    public void indexStub(Perl6SubCallStub stub, Map<String, String> frameworkData, IndexSink sink) {
        sink.occurrence(CroIndexKeys.CRO_ROUTES, frameworkData.get("path"));
    }

    @Override
    public void contributeSymbolNames(Project project, List<String> results) {
        results.addAll(CroRouteIndex.getInstance().getAllKeys(project));
    }

    @Override
    public void contributeSymbolItems(Project project, String pattern, List<NavigationItem> results) {
        CroRouteIndex routeIndex = CroRouteIndex.getInstance();
        for (String route : Filtering.simpleMatch(routeIndex.getAllKeys(project), pattern))
            results.addAll(routeIndex.get(route, project, GlobalSearchScope.projectScope(project)));
    }

    @Override
    public ItemPresentation getPresentation(Perl6PsiElement call, Map<String, String> frameworkData) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return frameworkData.get("method").toUpperCase() + " " + frameworkData.get("path");
            }

            @Nullable
            @Override
            public String getLocationString() {
                return call.getEnclosingPerl6ModuleName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Perl6Icons.CRO;
            }
        };
    }
}
