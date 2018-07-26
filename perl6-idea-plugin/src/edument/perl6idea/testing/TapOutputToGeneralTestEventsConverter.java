package edument.perl6idea.testing;

import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerException;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.Comment;
import org.tap4j.model.Directive;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;

import java.text.ParseException;
import java.util.List;

public class TapOutputToGeneralTestEventsConverter extends OutputToGeneralTestEventsConverter {
    @NotNull
    private TapConsumer myConsumer;
    private String currentTap = "";
    private ServiceMessageVisitor myVisitor;
    private String currentFile = "";

    public TapOutputToGeneralTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        super(testFrameworkName, consoleProperties);
        myConsumer = TapConsumerFactory.makeTap13YamlConsumer();
    }

    @Override
    protected boolean processServiceMessages(String text, final Key outputType, final ServiceMessageVisitor visitor) throws ParseException {
        if (visitor != null && myVisitor == null) {
            myVisitor = visitor;
            String theMatrix = new ServiceMessageBuilder("enteredTheMatrix").toString();
            handleMessageSend(theMatrix);
        }

        if (outputType == ProcessOutputTypes.STDOUT || outputType == ProcessOutputTypes.STDERR) {
            if (text.startsWith("====")) {
                currentFile = text.substring(4);
                if (!currentTap.isEmpty())
                    processTapOutput();
            } else {
                currentTap += text;
            }
        } else if (outputType == ProcessOutputTypes.SYSTEM && text.equals("\n")) {
            // Last time.
            processTapOutput();
        }
        return true;
    }

    private void processTapOutput() throws ParseException {
        if (!currentTap.isEmpty()) {
            TestSet set;
            super.processServiceMessages(ServiceMessageBuilder.testSuiteStarted(currentFile).toString(), ProcessOutputTypes.STDOUT, myVisitor);
            try {
                set = myConsumer.load(currentTap);
                processTestsCount(set);
                processSingleSuite(set.getTestResults());
            } catch (TapConsumerException e) {
                processBreakage();
            }
            super.processServiceMessages(ServiceMessageBuilder.testSuiteFinished(currentFile).toString(), ProcessOutputTypes.STDOUT, myVisitor);
            currentTap = "";
        }
    }

    private void processBreakage() throws ParseException {
        String name = "Test file died";
        handleMessageSend(ServiceMessageBuilder.testStarted(name).toString());
        String message = ServiceMessageBuilder.testFailed(name)
                                              .addAttribute("message", String.format("%s", currentTap)).toString();
        handleMessageSend(message);
        handleMessageSend(ServiceMessageBuilder.testFinished(name).toString());
    }

    private void processSingleSuite(List<TestResult> results) throws ParseException {
        for (TestResult result : results)
            processSingleTest(result);
    }

    private void processTestsCount(TestSet set) throws ParseException {
        String message = new ServiceMessageBuilder("testCount")
                .addAttribute("count", String.valueOf(set.getNumberOfTestResults())).toString();
        handleMessageSend(message);
    }

    private void processSingleTest(TestResult testResult) throws ParseException {
        String testName = String.format("%d %s", testResult.getTestNumber(), testResult.getDescription());
        Directive directive = testResult.getDirective();
        boolean hasSubtests = testResult.getSubtest() != null;
        if (hasSubtests) {
            handleMessageSend(ServiceMessageBuilder.testSuiteStarted(testName).toString());
            for (TestResult sub : testResult.getSubtest().getTestResults())
                processSingleTest(sub);
        } else
            handleMessageSend(ServiceMessageBuilder.testStarted(testName).toString());

        if (!hasSubtests && testResult.getStatus() == StatusValues.OK &&
            testResult.getDirective() == null) {
        } else if (!hasSubtests && ((directive != null && directive.getDirectiveValue() == DirectiveValues.SKIP) ||
                   (directive != null && directive.getDirectiveValue() == DirectiveValues.TODO))) {
            String message = ServiceMessageBuilder.testIgnored(testName)
                    .addAttribute("message", String.format("%s %s", testName, testResult.getDirective().getReason())).toString();
            handleMessageSend(message);
        } else if (!hasSubtests && testResult.getStatus() == StatusValues.NOT_OK) {
            StringBuilder errorMessage = new StringBuilder(testResult.getDescription() + "\n");
            for (Comment comment : testResult.getComments()) {
                errorMessage.append(comment.getText()).append("\n");
            }
            String message = ServiceMessageBuilder.testFailed(testName)
                    .addAttribute("error", "true")
                    .addAttribute("message", errorMessage.toString())
                                                  .toString();
            handleMessageSend(message);
        }
        if (hasSubtests)
            handleMessageSend(ServiceMessageBuilder.testSuiteFinished(testName).toString());
        else
            handleMessageSend(ServiceMessageBuilder.testFinished(testName).toString());
    }

    private void handleMessageSend(String message) throws ParseException {
        super.processServiceMessages(message, ProcessOutputTypes.STDOUT, myVisitor);
    }
}