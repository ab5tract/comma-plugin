package org.raku.comma.testing;

import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Key;
import com.intellij.util.ArrayUtil;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerException;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.*;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;

import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TapOutputToGeneralTestEventsConverter extends OutputToGeneralTestEventsConverter {
    public static final Pattern EXPECTED_GOT_PATTERN = Pattern.compile("expected: (.+?)\ngot: (.+?)\n", Pattern.DOTALL);
    public static final String TEST_HARNESS_PREFIX = "TEST_HARNESS_PREFIX";
    public static final String FILE_COMMAND = "file";
    private final List<String> myBasePaths;
    @NotNull
    private final TapConsumer myConsumer;
    private String currentTap = "";
    private ServiceMessageVisitor myVisitor;
    private String currentFile = "";

    public TapOutputToGeneralTestEventsConverter(@NotNull String testFrameworkName,
                                                 @NotNull TestConsoleProperties consoleProperties,
                                                 List<String> paths)
    {
        super(testFrameworkName, consoleProperties);
        myConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        myBasePaths = paths;
    }

    @Override
    protected boolean processServiceMessages(@NotNull String text,
                                             final @NotNull Key outputType,
                                             final @NotNull ServiceMessageVisitor visitor) throws ParseException
    {
        if (myVisitor == null) {
            myVisitor = visitor;
            String theMatrix = new ServiceMessageBuilder("enteredTheMatrix").toString();
            handleMessageSend(theMatrix);
        }

        if (outputType == ProcessOutputTypes.STDOUT || outputType == ProcessOutputTypes.STDERR) {
            if (text.startsWith(String.join(" ", TEST_HARNESS_PREFIX, FILE_COMMAND))) {
                currentFile = text.substring(TEST_HARNESS_PREFIX.length() + FILE_COMMAND.length() + 2,
                                             text.length() - 1); // 2 == spaces length
                if (!currentTap.isEmpty()) {
                    processTapOutput();
                }
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
            String testSuiteStarted = ServiceMessageBuilder
                    .testSuiteStarted(calculateSuiteName())
                    .addAttribute("locationHint", Paths.get(currentFile).toUri().toString()).toString();
            handleMessageSend(testSuiteStarted);
            try {
                set = myConsumer.load(currentTap);
                processTestsCount(set);
                processSingleSuite(set.getTapLines());
            } catch (TapConsumerException e) {
                processBreakage();
            }
            handleMessageSend(ServiceMessageBuilder.testSuiteFinished(calculateSuiteName()).toString());
            currentTap = "";
        }
    }

    @NotNull
    private String calculateSuiteName() {
        for (String path : myBasePaths) {
            if (currentFile.startsWith(path)) {
                return currentFile.substring(path.length() + 1);
            }
        }
        return currentFile;
    }

    private void processBreakage() throws ParseException {
        String name = "Test file died";
        handleMessageSend(ServiceMessageBuilder.testStarted(name).toString());
        String message = ServiceMessageBuilder.testFailed(name)
                                              .addAttribute("message", String.format("%s", currentTap))
                                              .toString();
        handleMessageSend(message);
        handleMessageSend(ServiceMessageBuilder.testFinished(name).toString());
    }

    private void processSingleSuite(List<TapElement> results) throws ParseException {
        // Apparently, Team City test protocol does not allow us
        // to send more than 1 `stdOutput` message, so we need to collect
        // all in a single string. `StringJoiner` here gives us neater
        // handling of `\n` between lines and uses fast StringBuilder internally
        StringJoiner stdOut = new StringJoiner("\n");

        // We calculate what TestElement is last (if there is one)
        // to later attach to it not only preceding text, but also following
        int lastTestIndex = -1;
        for (int i = 0, size = results.size(); i < size; i++) {
            TapElement res = results.get(i);
            if (res instanceof TestResult) {
                lastTestIndex = i;
            }
        }

        // We iterate though test suite here,
        // if iterated one is not last, we append possible
        // stdout output to it, but if we are at last one,
        // we break, because we want to collect all text output
        // that comes after it.
        TestResult savedLastTest = null;
        for (int i = 0, size = results.size(); i < size; i++) {
            TapElement result = results.get(i);

            if (i == lastTestIndex) {
                savedLastTest = (TestResult) result;
                break;
            }

            if (result instanceof TestResult) {
                processSingleTest((TestResult) result, (stdOut.length() == 0)
                                                       ? null
                                                       : stdOut + "\n");
                stdOut = new StringJoiner("\n");
            } else if (result instanceof Text) {
                stdOut.add(((Text) result).getValue());
            } else if (result instanceof Comment) {
                stdOut.add("# " + ((Comment) result).getText());
            }
        }

        /* Here we possibly can have:
         * `saveLastTest` can be null or not, depending on if there are any tests at all
         * `stdOut` can be empty or not, depending on if there is an output before last test
         * If we have no tests at all, but all `stdOut`, then create a dummy one and post it
         * If we have last test, it is a last element in `results` array, just send it with possible `stdOut`
         * If we have last test, it is not a last element, collect all output lines after it and send
         */

        if (savedLastTest == null && stdOut.length() != 0) {
            // Create a dummy test to contain output,
            // because testSuite cannot have an output sent
            savedLastTest = new TestResult(StatusValues.OK, 0);
            savedLastTest.setDescription("Output");
        }
        if (lastTestIndex != -1 && lastTestIndex != results.size() - 1) {
            // last test is present && more output follows after it
            for (int i = lastTestIndex + 1, size = results.size(); i < size; i++) {
                TapElement result = results.get(i);
                if (result instanceof Text) {
                    stdOut.add(((Text) results.get(i)).getValue());
                } else if (result instanceof Comment) {
                    stdOut.add("# " + ((Comment) results.get(i)).getText());
                }
            }
        }
        if (savedLastTest != null) {
            processSingleTest(savedLastTest, (stdOut.length() == 0)
                                             ? null
                                             : stdOut + "\n");
        }
    }

    private void processTestsCount(TestSet set) throws ParseException {
        String message = new ServiceMessageBuilder("testCount")
                .addAttribute("count", String.valueOf(set.getNumberOfTestResults())).toString();
        handleMessageSend(message);
    }

    private void processSingleTest(TestResult testResult, String stdOut) throws ParseException {
        String testName = String.format("%d %s", testResult.getTestNumber(), testResult.getDescription());
        Directive directive = testResult.getDirective();
        boolean hasSubtests = testResult.getSubtest() != null;
        if (hasSubtests) {
            handleMessageSend(ServiceMessageBuilder.testSuiteStarted(testName).toString());
            for (TestResult sub : testResult.getSubtest().getTestResults()) {
                processSingleTest(sub, stdOut);
                stdOut = null;
            }
        } else {
            handleMessageSend(ServiceMessageBuilder.testStarted(testName).toString());
            if (stdOut != null) {
                ServiceMessageBuilder testStdOut = ServiceMessageBuilder.testStdOut(testName);
                testStdOut.addAttribute("out", stdOut);
                handleMessageSend(testStdOut.toString());
            }
        }

        // If there is no subTests, the status was ok, and directive is null, then there is no message to send
        // (without this, we never know when the tests have finished)
        boolean needsToSendMessage = ! (!hasSubtests && (testResult.getStatus() == StatusValues.OK) && (testResult.getDirective() == null));
        if (needsToSendMessage) {
            if (!hasSubtests && ((directive != null && directive.getDirectiveValue() == DirectiveValues.SKIP)
                || (directive != null && directive.getDirectiveValue() == DirectiveValues.TODO)))
            {
                String testIgnored = ServiceMessageBuilder.testIgnored(testName)
                                                          .addAttribute("message",
                                                                        String.format("%s %s", testName, testResult.getDirective()
                                                                                                                   .getReason()))
                                                          .toString();
                handleMessageSend(testIgnored);
            } else if (!hasSubtests && testResult.getStatus() == StatusValues.NOT_OK) {
                StringBuilder errorMessageBuilder = new StringBuilder(testResult.getDescription() + "\n");
                for (Comment comment : testResult.getComments()) {
                    errorMessageBuilder.append(comment.getText()).append("\n");
                }

                String errorMessage = errorMessageBuilder.toString();
                String[] expectedAndActual = checkIfComparisonFailure(errorMessage);

                ServiceMessageBuilder testFailed = ServiceMessageBuilder.testFailed(testName)
                                                                        .addAttribute("error", "true")
                                                                        .addAttribute("message", errorMessage);
                if (expectedAndActual.length != 0) {
                    testFailed.addAttribute("type", "comparisonFailure")
                              .addAttribute("expected", expectedAndActual[0])
                              .addAttribute("actual", expectedAndActual[1]);
                }
                handleMessageSend(testFailed.toString());
            }
        }

        if (hasSubtests) {
            handleMessageSend(ServiceMessageBuilder.testSuiteFinished(testName).toString());
        } else {
            handleMessageSend(ServiceMessageBuilder.testFinished(testName).toString());
        }
    }

    private static String[] checkIfComparisonFailure(String message) {
        Matcher matcher = EXPECTED_GOT_PATTERN.matcher(message);
        if (matcher.find()) {
            return new String[]{matcher.group(1), matcher.group(2)};
        }
        return ArrayUtil.EMPTY_STRING_ARRAY;
    }

    private void handleMessageSend(String message) throws ParseException {
        super.processServiceMessages(message, ProcessOutputTypes.STDOUT, myVisitor);
    }
}
