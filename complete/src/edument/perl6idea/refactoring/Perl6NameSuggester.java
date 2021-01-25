package edument.perl6idea.refactoring;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Perl6NameSuggester {
  /**
   * Handles 4 different cases: (no PascalCase)
   * - UPPERCASE-EVERYTHING
   * - snake-case
   * - under_score
   * - camelCase
   */
  public static Collection<String> getNamePieces(String name, String type) {
    String sigil = getSigil(type);
    if (isCamelCase(name)) {
      return getCamelCasePieces(name, sigil);
    }
    return getSepPieces(name, sigil, name.contains("-") ? "-" : "_");
  }

  static String[] atPrefixes = {"List[", "Array[", "Positional[", "Seq[", "Iterable["};

  @NotNull
  private static String getSigil(String type) {
    if (type.startsWith("Hash[") || type.startsWith("Map["))
      return "%";
    if (Arrays.stream(atPrefixes).anyMatch(prefix -> type.startsWith(prefix)))
      return "@";
    return "$";
  }

  private static boolean isCamelCase(String name) {
    return name.matches("[a-z]+([A-Z]+[a-z]+)+");
  }

  private static Collection<String> getCamelCasePieces(String name, String sigil) {
    if (name.matches("^get[A-Z].*")) {
      name = name.substring(3);
    }
    if (name.matches("^[iI]s[A-Z].*")) {
      name = name.substring(2);
    }
    String[] splits = name.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    List<List<String>> sublists = generateSublists(Arrays.asList(splits));
    return ContainerUtil.map(sublists, (List<String> parts) -> {
      String firstPart = parts.get(0);
      if (firstPart.matches("[A-Z]+")) // if we have a part in ALL CAPS at the beginning lower-case it
        firstPart = firstPart.toLowerCase(Locale.ENGLISH);
      else
        firstPart = Character.toLowerCase(firstPart.charAt(0)) + firstPart.substring(1);
      return sigil + firstPart + StringUtil.join(parts.subList(1, parts.size()), "");
    });
  }

  private static Collection<String> getSepPieces(String name, String sigil, String sep) {
    name = StringUtil.trimStart(name, "get" + sep);
    name = StringUtil.trimStart(name, "is" + sep);
    name = StringUtil.trimStart(name, sep);
    return processSplits(StringUtil.split(name, sep), sigil, sep);
  }

  private static List<String> processSplits(List<String> splits, String sigil, String sep) {
    return ContainerUtil.map(generateSublists(splits), p -> sigil + StringUtil.join(p, sep));
  }

  private static List<List<String>> generateSublists(List<String> splits) {
    ArrayList<List<String>> subs = new ArrayList<>();
    for (int i = 0; i < splits.size(); ++i) {
      subs.add(splits.subList(i, splits.size()));
    }
    return subs;
  }

  public static Collection<String> suggest(PsiElement element) {
    if (element instanceof Perl6Statement) {
      PsiElement lastChild = Perl6PsiUtil.getLastNonSpaceChild(element);
      if (lastChild != null) {
        return suggest(lastChild);
      }
    }
    if (element instanceof Perl6PostfixApplication) {
      return suggest(((Perl6PostfixApplication)element).getPostfix());
    }
    if (element instanceof Perl6MethodCall) {
      return getNamePieces(((Perl6MethodCall)element).getSimpleName().getText(), ((Perl6MethodCall)element).inferType().nominalType().getName());
    }
    if (element instanceof P6CodeBlockCall && element instanceof Perl6PsiElement) {
      return getNamePieces(((P6CodeBlockCall)element).getCallName(), ((Perl6PsiElement)element).inferType().nominalType().getName());
    }
    // Try out `$xN` until we find the available one
    String base = "$x", name = base;

    Perl6PsiElement el = PsiTreeUtil.getNonStrictParentOfType(element, Perl6PsiElement.class);
    // If we are not on a Raku element, something is very wrong, so just return what we have
    // and hope for the best
    if (el == null)
      return new ArrayList<>(Collections.singletonList(name));
    int counter = 1;
    // Try to look up if $x1, $x2, $x3... are present lexically until we find
    // a name not yet taken, then suggest that.
    while (true) {
      Perl6Symbol symbol = el.resolveLexicalSymbol(Perl6SymbolKind.Variable, name);
      if (symbol == null)
        return new ArrayList<>(Collections.singletonList(name));
      else
        name = base + counter;
      counter++;
    }
  }
}
