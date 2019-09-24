package edument.perl6idea.psi.stub;

import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.stub.impl.Perl6FileStubImpl;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Perl6FileElementType extends IStubFileElementType<Perl6FileStub> {
    public static final int STUB_VERSION = 27;

    public Perl6FileElementType() {
        super(Perl6Language.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return STUB_VERSION;
    }

    @Override
    public StubBuilder getBuilder() {
        return new Perl6FileStubBuilder();
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.file";
    }

    @Override
    public void serialize(@NotNull final Perl6FileStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getCompilationUnitName());
        Map<Integer, List<Integer>> lineMap = stub.getStatementLineMap();
        dataStream.writeInt(lineMap.size());
        for (Map.Entry<Integer, List<Integer>> line : lineMap.entrySet()) {
            dataStream.writeInt(line.getKey());
            List<Integer> lineList = line.getValue();
            dataStream.writeInt(lineList.size());
            for (Integer lineNumber : lineList)
                dataStream.writeInt(lineNumber);
        }
    }

    @NotNull
    @Override
    public Perl6FileStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef compilationUnitName = dataStream.readName();
        int numLineMapEntries = dataStream.readInt();
        Map<Integer, List<Integer>> lineMap = new HashMap<>(numLineMapEntries);
        for (int i = 0; i < numLineMapEntries; i++) {
            int lineNumber = dataStream.readInt();
            int numMappings = dataStream.readInt();
            List<Integer> mappings = new ArrayList<>(numMappings);
            for (int j = 0; j < numMappings; j++)
                mappings.add(dataStream.readInt());
            lineMap.put(lineNumber, mappings);
        }
        return new Perl6FileStubImpl(null, compilationUnitName == null ? null : compilationUnitName.getString(), lineMap);
    }

    @Override
    public void indexStub(@NotNull final Perl6FileStub stub, @NotNull final IndexSink sink) {
        String compUnitName = stub.getCompilationUnitName();
        if (compUnitName != null)
            sink.occurrence(Perl6StubIndexKeys.PROJECT_MODULES, compUnitName);
    }
}
