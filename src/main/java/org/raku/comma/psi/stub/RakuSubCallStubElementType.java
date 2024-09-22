package org.raku.comma.psi.stub;

import com.intellij.psi.stubs.*;
import org.raku.comma.RakuLanguage;
import org.raku.comma.extensions.RakuFrameworkCall;
import org.raku.comma.psi.RakuSubCall;
import org.raku.comma.psi.impl.RakuSubCallImpl;
import org.raku.comma.psi.stub.impl.RakuSubCallStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RakuSubCallStubElementType extends IStubElementType<RakuSubCallStub, RakuSubCall> {
    public RakuSubCallStubElementType() {
        super("SUBCALL", RakuLanguage.INSTANCE);
    }

    @Override
    public RakuSubCall createPsi(@NotNull RakuSubCallStub stub) {
        return new RakuSubCallImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuSubCallStub createStub(@NotNull RakuSubCall call, StubElement parentStub) {
        RakuFrameworkCall[] extensions = RakuFrameworkCall.EP_NAME.getExtensions();
        String calleeName = call.getCallName();
        Map<String, String> frameworkData = new HashMap<>();
        for (RakuFrameworkCall ext : extensions) {
            if (ext.isApplicable(call)) {
                String name = ext.getFrameworkName();
                for (Map.Entry<String, String> entry : ext.getFrameworkData(call).entrySet()) {
                    frameworkData.put(name + "." + entry.getKey(), entry.getValue());
                }
            }
        }
        return new RakuSubCallStubImpl(parentStub, calleeName, frameworkData);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.subcall";
    }

    @Override
    public void serialize(@NotNull RakuSubCallStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        Map<String, String> frameworkData = stub.getAllFrameworkData();
        dataStream.writeInt(frameworkData.size());
        for (Map.Entry<String, String> data : frameworkData.entrySet()) {
            dataStream.writeName(data.getKey());
            dataStream.writeUTF(data.getValue());
        }
    }

    @NotNull
    @Override
    public RakuSubCallStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String name = Objects.requireNonNull(dataStream.readName()).getString();
        Map<String, String> frameworkData = new HashMap<>();
        int elements = dataStream.readInt();
        for (int i = 0; i < elements; i++) {
            String key = Objects.requireNonNull(dataStream.readName()).getString();
            String value = dataStream.readUTF();
            frameworkData.put(key, value);
        }
        return new RakuSubCallStubImpl(parentStub, name, frameworkData);
    }

    @Override
    public void indexStub(@NotNull RakuSubCallStub stub, @NotNull IndexSink sink) {
        RakuFrameworkCall[] extensions = RakuFrameworkCall.EP_NAME.getExtensions();
        Map<String, String> allFrameworkData = stub.getAllFrameworkData();
        for (RakuFrameworkCall ext : extensions) {
            String prefix = ext.getFrameworkName();
            Map<String, String> frameworkData = new HashMap<>();
            for (Map.Entry<String, String> entry : allFrameworkData.entrySet()) {
                if (entry.getKey().startsWith(prefix + ".")) {
                    frameworkData.put(entry.getKey().substring(prefix.length() + 1), entry.getValue());
                }
            }
            if (! frameworkData.isEmpty()) {
                ext.indexStub(stub, frameworkData, sink);
            }
        }
    }
}
