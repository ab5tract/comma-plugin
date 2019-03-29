package edument.perl6idea.psi.stub;

import com.intellij.openapi.extensions.AreaInstance;
import com.intellij.psi.stubs.*;
import com.intellij.util.containers.hash.HashMap;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.extensions.Perl6FrameworkCall;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.impl.Perl6SubCallImpl;
import edument.perl6idea.psi.stub.impl.Perl6SubCallStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

public class Perl6SubCallStubElementType extends IStubElementType<Perl6SubCallStub, Perl6SubCall> {
    public Perl6SubCallStubElementType() {
        super("SUBCALL", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6SubCall createPsi(@NotNull Perl6SubCallStub stub) {
        return new Perl6SubCallImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6SubCallStub createStub(@NotNull Perl6SubCall call, StubElement parentStub) {
        Perl6FrameworkCall[] extensions = Perl6FrameworkCall.FRAMEWORK_CALL_EP.getExtensions();
        String calleeName = call.getCalleeName();
        Map<String, String> frameworkData = new HashMap<>();
        for (Perl6FrameworkCall ext : extensions) {
            String name = ext.getFrameworkName();
            for (Map.Entry<String, String> entry : ext.getFrameworkData(call).entrySet()) {
                frameworkData.put(name + "." + entry.getKey(), entry.getValue());
            }
        }
        return new Perl6SubCallStubImpl(parentStub, calleeName, frameworkData);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.subcall";
    }

    @Override
    public void serialize(@NotNull Perl6SubCallStub stub, @NotNull StubOutputStream dataStream) throws IOException {
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
    public Perl6SubCallStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String name = dataStream.readName().getString();
        Map<String, String> frameworkData = new HashMap<>();
        int elements = dataStream.readInt();
        for (int i = 0; i < elements; i++) {
            String key = dataStream.readName().getString();
            String value = dataStream.readUTF();
            frameworkData.put(key, value);
        }
        return new Perl6SubCallStubImpl(parentStub, name, frameworkData);
    }

    @Override
    public void indexStub(@NotNull Perl6SubCallStub stub, @NotNull IndexSink sink) {

    }
}
