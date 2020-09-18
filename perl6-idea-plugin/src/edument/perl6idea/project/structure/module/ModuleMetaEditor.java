package edument.perl6idea.project.structure.module;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleMetaEditor implements ModuleConfigurationEditor {
    public static final String MISSING_FIELD_MESSAGE = "Does not exist, must be set!";
    private static final String[] SPDX_LICENSE_NAMES = new String[]{
        "0BSD", "AAL", "Abstyles", "Adobe-2006", "Adobe-Glyph", "ADSL", "AFL-1.1", "AFL-1.2", "AFL-2.0", "AFL-2.1", "AFL-3.0", "Afmparse",
        "AGPL-1.0-only", "AGPL-1.0-or-later", "AGPL-3.0-only", "AGPL-3.0-or-later", "Aladdin", "AMDPLPA", "AML", "AMPAS", "ANTLR-PD",
        "Apache-1.0", "Apache-1.1", "Apache-2.0", "APAFML", "APL-1.0", "APSL-1.0", "APSL-1.1", "APSL-1.2", "APSL-2.0", "Artistic-1.0",
        "Artistic-1.0-cl8", "Artistic-1.0-Perl", "Artistic-2.0", "Bahyph", "Barr", "Beerware", "BitTorrent-1.0", "BitTorrent-1.1",
        "blessing", "BlueOak-1.0.0", "Borceux", "BSD-1-Clause", "BSD-2-Clause", "BSD-2-Clause-FreeBSD", "BSD-2-Clause-Patent",
        "BSD-3-Clause", "BSD-3-Clause-Attribution", "BSD-3-Clause-Clear", "BSD-3-Clause-LBNL", "BSD-3-Clause-No-Nuclear-License",
        "BSD-3-Clause-No-Nuclear-License-2014", "BSD-3-Clause-No-Nuclear-Warranty", "BSD-3-Clause-Open-MPI", "BSD-4-Clause",
        "BSD-4-Clause-UC", "BSD-Protection", "BSD-Source-Code", "BSL-1.0", "bzip2-1.0.5", "bzip2-1.0.6", "CAL-1.0",
        "CAL-1.0-Combined-Work-Exception", "Caldera", "CATOSL-1.1", "CC-BY-1.0", "CC-BY-2.0", "CC-BY-2.5", "CC-BY-3.0", "CC-BY-4.0",
        "CC-BY-NC-1.0", "CC-BY-NC-2.0", "CC-BY-NC-2.5", "CC-BY-NC-3.0", "CC-BY-NC-4.0", "CC-BY-NC-ND-1.0", "CC-BY-NC-ND-2.0",
        "CC-BY-NC-ND-2.5", "CC-BY-NC-ND-3.0", "CC-BY-NC-ND-4.0", "CC-BY-NC-SA-1.0", "CC-BY-NC-SA-2.0", "CC-BY-NC-SA-2.5", "CC-BY-NC-SA-3.0",
        "CC-BY-NC-SA-4.0", "CC-BY-ND-1.0", "CC-BY-ND-2.0", "CC-BY-ND-2.5", "CC-BY-ND-3.0", "CC-BY-ND-4.0", "CC-BY-SA-1.0", "CC-BY-SA-2.0",
        "CC-BY-SA-2.5", "CC-BY-SA-3.0", "CC-BY-SA-4.0", "CC-PDDC", "CC0-1.0", "CDDL-1.0", "CDDL-1.1", "CDLA-Permissive-1.0",
        "CDLA-Sharing-1.0", "CECILL-1.0", "CECILL-1.1", "CECILL-2.0", "CECILL-2.1", "CECILL-B", "CECILL-C", "CERN-OHL-1.1", "CERN-OHL-1.2",
        "CERN-OHL-P-2.0", "CERN-OHL-S-2.0", "CERN-OHL-W-2.0", "ClArtistic", "CNRI-Jython", "CNRI-Python", "CNRI-Python-GPL-Compatible",
        "Condor-1.1", "copyleft-next-0.3.0", "copyleft-next-0.3.1", "CPAL-1.0", "CPL-1.0", "CPOL-1.02", "Crossword", "CrystalStacker",
        "CUA-OPL-1.0", "Cube", "curl", "D-FSL-1.0", "diffmark", "DOC", "Dotseqn", "DSDP", "dvipdfm", "ECL-1.0", "ECL-2.0", "EFL-1.0",
        "EFL-2.0", "eGenix", "Entessa", "EPL-1.0", "EPL-2.0", "ErlPL-1.1", "etalab-2.0", "EUDatagrid", "EUPL-1.0", "EUPL-1.1", "EUPL-1.2",
        "Eurosym", "Fair", "Frameworx-1.0", "FreeImage", "FSFAP", "FSFUL", "FSFULLR", "FTL", "GFDL-1.1-only", "GFDL-1.1-or-later",
        "GFDL-1.2-only", "GFDL-1.2-or-later", "GFDL-1.3-only", "GFDL-1.3-or-later", "Giftware", "GL2PS", "Glide", "Glulxe", "gnuplot",
        "GPL-1.0-only", "GPL-1.0-or-later", "GPL-2.0-only", "GPL-2.0-or-later", "GPL-3.0-only", "GPL-3.0-or-later", "gSOAP-1.3b",
        "HaskellReport", "Hippocratic-2.1", "HPND", "HPND-sell-variant", "IBM-pibs", "ICU", "IJG", "ImageMagick", "iMatix", "Imlib2",
        "Info-ZIP", "Intel", "Intel-ACPI", "Interbase-1.0", "IPA", "IPL-1.0", "ISC", "JasPer-2.0", "JPNIC", "JSON", "LAL-1.2", "LAL-1.3",
        "Latex2e", "Leptonica", "LGPL-2.0-only", "LGPL-2.0-or-later", "LGPL-2.1-only", "LGPL-2.1-or-later", "LGPL-3.0-only",
        "LGPL-3.0-or-later", "LGPLLR", "Libpng", "libpng-2.0", "libselinux-1.0", "libtiff", "LiLiQ-P-1.1", "LiLiQ-R-1.1", "LiLiQ-Rplus-1.1",
        "Linux-OpenIB", "LPL-1.0", "LPL-1.02", "LPPL-1.0", "LPPL-1.1", "LPPL-1.2", "LPPL-1.3a", "LPPL-1.3c", "MakeIndex", "MirOS", "MIT",
        "MIT-0", "MIT-advertising", "MIT-CMU", "MIT-enna", "MIT-feh", "MITNFA", "Motosoto", "mpich2", "MPL-1.0", "MPL-1.1", "MPL-2.0",
        "MPL-2.0-no-copyleft-exception", "MS-PL", "MS-RL", "MTLL", "MulanPSL-1.0", "MulanPSL-2.0", "Multics", "Mup", "NASA-1.3", "Naumen",
        "NBPL-1.0", "NCGL-UK-2.0", "NCSA", "Net-SNMP", "NetCDF", "Newsletr", "NGPL", "NLOD-1.0", "NLPL", "Nokia", "NOSL", "Noweb",
        "NPL-1.0", "NPL-1.1", "NPOSL-3.0", "NRL", "NTP", "NTP-0", "O-UDA-1.0", "OCCT-PL", "OCLC-2.0", "ODbL-1.0", "ODC-By-1.0", "OFL-1.0",
        "OFL-1.0-no-RFN", "OFL-1.0-RFN", "OFL-1.1", "OFL-1.1-no-RFN", "OFL-1.1-RFN", "OGC-1.0", "OGL-Canada-2.0", "OGL-UK-1.0",
        "OGL-UK-2.0", "OGL-UK-3.0", "OGTSL", "OLDAP-1.1", "OLDAP-1.2", "OLDAP-1.3", "OLDAP-1.4", "OLDAP-2.0", "OLDAP-2.0.1", "OLDAP-2.1",
        "OLDAP-2.2", "OLDAP-2.2.1", "OLDAP-2.2.2", "OLDAP-2.3", "OLDAP-2.4", "OLDAP-2.5", "OLDAP-2.6", "OLDAP-2.7", "OLDAP-2.8", "OML",
        "OpenSSL", "OPL-1.0", "OSET-PL-2.1", "OSL-1.0", "OSL-1.1", "OSL-2.0", "OSL-2.1", "OSL-3.0", "Parity-6.0.0", "Parity-7.0.0",
        "PDDL-1.0", "PHP-3.0", "PHP-3.01", "Plexus", "PolyForm-Noncommercial-1.0.0", "PolyForm-Small-Business-1.0.0", "PostgreSQL",
        "PSF-2.0", "psfrag", "psutils", "Python-2.0", "Qhull", "QPL-1.0", "Rdisc", "RHeCos-1.1", "RPL-1.1", "RPL-1.5", "RPSL-1.0", "RSA-MD",
        "RSCPL", "Ruby", "SAX-PD", "Saxpath", "SCEA", "Sendmail", "Sendmail-8.23", "SGI-B-1.0", "SGI-B-1.1", "SGI-B-2.0", "SHL-0.5",
        "SHL-0.51", "SimPL-2.0", "SISSL", "SISSL-1.2", "Sleepycat", "SMLNJ", "SMPPL", "SNIA", "Spencer-86", "Spencer-94", "Spencer-99",
        "SPL-1.0", "SSH-OpenSSH", "SSH-short", "SSPL-1.0", "SugarCRM-1.1.3", "SWL", "TAPR-OHL-1.0", "TCL", "TCP-wrappers", "TMate",
        "TORQUE-1.1", "TOSL", "TU-Berlin-1.0", "TU-Berlin-2.0", "UCL-1.0", "Unicode-DFS-2015", "Unicode-DFS-2016", "Unicode-TOU",
        "Unlicense", "UPL-1.0", "Vim", "VOSTROM", "VSL-1.0", "W3C", "W3C-19980720", "W3C-20150513", "Watcom-1.0", "Wsuipa", "WTFPL", "X11",
        "Xerox", "XFree86-1.1", "xinetd", "Xnet", "xpp", "XSkat", "YPL-1.0", "YPL-1.1", "Zed", "Zend-2.0", "Zimbra-1.3", "Zimbra-1.4",
        "Zlib", "zlib-acknowledgement", "ZPL-1.1", "ZPL-2.0", "ZPL-2.1"
    };
    private final Module myModule;
    private JComponent mySettingsPanel;
    private JTextField myNameField;
    private JTextField myVersionField;
    private JTextField myDescriptionField;
    private JTextField myAuthField;
    private TextFieldWithAutoCompletion<String> myLicenseField;
    private final Map<String, String> myMeta = new HashMap<>();
    private static final String[] keys = new String[]{
        "name", "version", "auth", "description", "license", "source", "authors"
    };
    private JTextField mySourceURLField;
    private JTextField myAuthorsField;
    private final Set<String> myMissingFields = new HashSet<>();
    private Set<String> myEmptyFields = new HashSet<>();

    public ModuleMetaEditor(ModuleConfigurationState state) {
        myModule = state.getRootModel().getModule();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Metadata";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (mySettingsPanel == null) {
            mySettingsPanel = new JPanel(new MigLayout());
            createFields();
        }
        return mySettingsPanel;
    }

    private void createFields() {
        mySettingsPanel.add(new JLabel("Name:"));
        myNameField = new JTextField(80);
        addListener(myNameField, "name");
        mySettingsPanel.add(myNameField, "wrap");

        mySettingsPanel.add(new JLabel("Description:"));
        myDescriptionField = new JTextField(80);
        addListener(myDescriptionField, "description");
        mySettingsPanel.add(myDescriptionField, "wrap");

        mySettingsPanel.add(new JLabel("Version:"));
        myVersionField = new JTextField(80);
        addListener(myVersionField, "version");
        mySettingsPanel.add(myVersionField, "wrap");

        mySettingsPanel.add(new JLabel("Auth:"));
        myAuthField = new JTextField(80);
        addListener(myAuthField, "auth");
        mySettingsPanel.add(myAuthField, "wrap");

        mySettingsPanel.add(new JLabel("License:"));
        myLicenseField = new TextFieldWithAutoCompletion<>(myModule.getProject(),
                                                           new TextFieldWithAutoCompletion.StringsCompletionProvider(new HashSet<>(),
                                                                                                                     null), true, null);
        myLicenseField.setPreferredWidth(1000);
        initLicenseComboBox();
        mySettingsPanel.add(myLicenseField, "wrap");

        mySettingsPanel.add(new JLabel("Source URL:"));
        mySourceURLField = new JTextField(80);
        addListener(mySourceURLField, "source");
        mySettingsPanel.add(mySourceURLField, "wrap");

        mySettingsPanel.add(new JLabel("Authors (comma separated):"));
        myAuthorsField = new JTextField(80);
        addListener(myAuthorsField, "authors");
        mySettingsPanel.add(myAuthorsField, "wrap");
    }

    private void initLicenseComboBox() {
        myLicenseField.setVariants(Arrays.asList(SPDX_LICENSE_NAMES));
        String key = "license";
        myLicenseField.addDocumentListener(new com.intellij.openapi.editor.event.DocumentListener() {
            @Override
            public void documentChanged(com.intellij.openapi.editor.event.@NotNull DocumentEvent event) {
                checkValue(event.getDocument().getText());
            }

            private void checkValue(String text) {
                if (text.isEmpty()) {
                    myEmptyFields.add(key);
                }
                else {
                    myEmptyFields.remove(key);
                }

                if (text.equals(MISSING_FIELD_MESSAGE)) {
                    myMissingFields.add(key);
                }
                else {
                    myMissingFields.remove(key);
                }
            }
        });
    }

    private void addListener(JTextField field, String key) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkValue();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkValue();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkValue();
            }

            private void checkValue() {
                if (field.getText().isEmpty())
                    myEmptyFields.add(key);
                else
                    myEmptyFields.remove(key);

                if (field.getText().equals(MISSING_FIELD_MESSAGE))
                    myMissingFields.add(key);
                else
                    myMissingFields.remove(key);
            }
        });
    }

    @Override
    public boolean isModified() {
        return !gatherFields().equals(myMeta);
    }

    @Override
    public void reset() {
        Perl6MetaDataComponent metaData = myModule.getService(Perl6MetaDataComponent.class);
        if (metaData.isMetaDataExist()) {
            String name = metaData.getName();
            myMeta.put("name", name == null ? MISSING_FIELD_MESSAGE : name);
            String description = metaData.getDescription();
            myMeta.put("description", description == null ? MISSING_FIELD_MESSAGE : description);
            String version = metaData.getVersion();
            myMeta.put("version", version == null ? MISSING_FIELD_MESSAGE : version);
            String auth = metaData.getAuth();
            myMeta.put("auth", auth == null ? MISSING_FIELD_MESSAGE : auth);
            String license = metaData.getLicense();
            myMeta.put("license", license == null ? MISSING_FIELD_MESSAGE : license);
            String source = metaData.getSourceURL();
            myMeta.put("source-url", source == null ? MISSING_FIELD_MESSAGE : source);
            List<Object> authors = metaData.getAuthors();
            String authorsString = authors == null ? MISSING_FIELD_MESSAGE :
                                   authors.stream().filter(s -> s instanceof String).map(s -> (String)s).collect(Collectors.joining(","));
            myMeta.put("authors", authorsString);
        } else {
            for (String key : keys)
                myMeta.put(key, MISSING_FIELD_MESSAGE);
        }
        populateFields();
    }

    private void populateFields() {
        myNameField.setText(myMeta.get("name"));
        myDescriptionField.setText(myMeta.get("description"));
        myVersionField.setText(myMeta.get("version"));
        myAuthField.setText(myMeta.get("auth"));
        myLicenseField.setText(myMeta.get("license"));
        mySourceURLField.setText(myMeta.get("source-url"));
        myAuthorsField.setText(myMeta.get("authors"));
    }

    private Map<String, String> gatherFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", myNameField.getText());
        fields.put("description", myDescriptionField.getText());
        fields.put("version", myVersionField.getText());
        fields.put("auth", myAuthField.getText());
        fields.put("license", myLicenseField.getText());
        fields.put("source-url", mySourceURLField.getText());
        fields.put("authors", myAuthorsField.getText());
        return fields;
    }

    @Override
    public void apply() throws ConfigurationException {
        // META6.json exists, but has missing fields
        if (!myMissingFields.isEmpty())
            throw new ConfigurationException("Missing fields are present: " + String.join(", ", myMissingFields));
        // META6.json does not exist at all
        myEmptyFields = new HashSet<>();
        gatherFields().forEach((k, v) -> {
            if (v.isEmpty()) myEmptyFields.add(k);
        });
        if (!myEmptyFields.isEmpty())
            throw new ConfigurationException("Empty fields: " + String.join(", ", ArrayUtil.toStringArray(myEmptyFields)));
        try {
            saveFields();
        }
        catch (IOException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    private void saveFields() throws IOException {
        Perl6MetaDataComponent metaData = myModule.getService(Perl6MetaDataComponent.class);
        if (!metaData.isMetaDataExist()) {
            metaData.createStubMetaFile(myModule.getName(), null, false);
        }
        ApplicationManager.getApplication().invokeLater(() -> WriteAction.run(() -> {
            metaData.setName(myNameField.getText());
            metaData.setDescription(myDescriptionField.getText());
            metaData.setAuth(myAuthField.getText());
            metaData.setVersion(myVersionField.getText());
            metaData.setLicense(myLicenseField.getText());
            metaData.setSourceURL(mySourceURLField.getText());
            metaData.setAuthors(ContainerUtil.map(myAuthorsField.getText().split(","), s -> s.trim()));
        }));
    }
}
