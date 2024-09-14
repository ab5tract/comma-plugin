package org.raku.pm;

import com.intellij.openapi.components.*;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.pm.impl.RakuPakkuPM;
import org.raku.pm.impl.RakuZefPM;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service(Service.Level.PROJECT)
@State(name = "org.raku.pm.RakuPackageManagerManager", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public final class RakuPackageManagerManager implements PersistentStateComponent<Element> {
    private RakuPackageManager currentPM;
    private List<RakuPackageManager> pmList = new ArrayList<>();

    @Override
    public @Nullable Element getState() {
        Element el = new Element("pmm");

        if (currentPM != null) {
            Element currElement = formPMElement(currentPM);
            el.addContent(currElement);
        }

        Element pmListElement = new Element("pmList");
        for (RakuPackageManager manager : pmList)
            pmListElement.addContent(formPMElement(manager));
        el.addContent(pmListElement);

        return el;
    }

    public void addNewPM(VirtualFile location, boolean setAsCurrent) {
        for (RakuPackageManager manager : pmList) {
            if (manager.location.equals(location.getPath())) {
                pmList.remove(manager);
                break;
            }
        }
        RakuPackageManager newPM = parsePM(location.getName(), location.getPath());
        pmList.add(newPM);
        if (setAsCurrent)
            currentPM = newPM;
    }

    public void setPM(RakuPackageManager pm) {
        currentPM = pm;
    }

    public RakuPackageManager getCurrentPM() {
        return currentPM;
    }

    public List<RakuPackageManager> getPMList() {
        return pmList;
    }

    public static boolean isManagerFile(VirtualFile file) {
        return file.getName().startsWith("pakku") || file.getName().startsWith("zef");
    }

    @NotNull
    private static Element formPMElement(RakuPackageManager pm) {
        Element el = new Element("current");
        el.setAttribute("kind", pm.getKind().name());
        el.setAttribute("location", pm.getLocation());
        return el;
    }

    @Override
    public void loadState(@NotNull Element state) {
        pmList = new ArrayList<>();
        List<Element> elems = state.getChildren();
        for (Element elem : elems) {
            if (elem.getName().equals("current")) {
                String kind = elem.getAttributeValue("kind");
                String location = elem.getAttributeValue("location");
                if (kind != null && location != null)
                    currentPM = parsePM(kind, location);
            }
            else if (elem.getName().equals("pmList")) {
                for (Element pmInstance : elem.getChildren()) {
                    String kind = pmInstance.getAttributeValue("kind");
                    String location = pmInstance.getAttributeValue("location");
                    if (kind != null && location != null) {
                        RakuPackageManager manager = parsePM(kind, location);
                        if (manager != null)
                            pmList.add(manager);
                    }
                }
            }
        }
    }

    public static RakuPackageManager parsePM(@NotNull String kind, @NotNull String location) {
        RakuPackageManager pm = switch (RakuPackageManagerKind.valueOf(kind.toUpperCase(Locale.ENGLISH))) {
            case ZEF    -> new RakuZefPM(location);
            case PAKKU  -> new RakuPakkuPM(location);
            default     -> throw new IllegalArgumentException("Unknown kind of Package Manager");
        };
        return pm;
    }

    public static void detectPMs(List<SuggestedItem> detected) {
        Map<RakuPackageManagerKind, String[]> execNames = new HashMap<>();
        execNames.put(RakuPackageManagerKind.ZEF, new String[]{"zef", "zef.exe", "zef.bat"});
        execNames.put(RakuPackageManagerKind.PAKKU, new String[]{"pakku", "pakku.exe", "pakku.bat"});

        for (RakuPackageManagerKind kind : execNames.keySet()) {
            String[] strings = execNames.get(kind);
            for (String name : strings) {
                for (String path : System.getenv("PATH").split(":")) {
                    Path pathWithName = Paths.get(path, name);
                    File file = pathWithName.toFile();
                    if (file.exists() && file.canExecute()) {
                        detected.add(new SuggestedItem(kind, pathWithName.toString()));
                    }
                }
            }
        }
    }

    public static class PMInstanceData {
        public RakuPackageManagerKind kind;
        public String location;

        public PMInstanceData(RakuPackageManagerKind kind, String location) {
            this.kind = kind;
            this.location = location;
        }

        public PMInstanceData(RakuPackageManager pm) {
            this.kind = pm.getKind();
            this.location = pm.getLocation();
        }

        public RakuPackageManager toPM() {
            return parsePM(kind.getName().toUpperCase(Locale.ROOT), location);
        }
    }

    public static class SuggestedItem extends RakuPackageManagerManager.PMInstanceData {
        public SuggestedItem(RakuPackageManagerKind kind, String location) {
            super(kind, location);
        }
    }
}
