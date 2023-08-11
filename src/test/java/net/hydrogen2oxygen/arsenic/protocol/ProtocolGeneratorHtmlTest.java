package net.hydrogen2oxygen.arsenic.protocol;

import net.hydrogen2oxygen.arsenic.*;
import net.hydrogen2oxygen.arsenic.exceptions.CleanUpException;
import net.hydrogen2oxygen.arsenic.exceptions.PreconditionsException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class ProtocolGeneratorHtmlTest {

    @BeforeAll
    public static void init() throws IOException {
        FileUtils.copyFile(
                new File("src/test/resources/images/screenshot.png"),
                new File("target/protocols/screenshot.png"));
    }

    @Test
    void test() throws Exception {

        File targetFolder = new File("target");
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        System.setProperty("environment", "exampleEnvironment.json");
        Environment environment = Se.loadEnvironment();

        Group group = new Group("TestGroup", environment);
        group.add(new TestAutomation());

        Parallel parallel = new Parallel("ParallelTest", environment);
        parallel.add(group);
        parallel.run();

        ProtocolGeneratorHtml generatorHtml = new ProtocolGeneratorHtml();
        String html = generatorHtml.generateHtml(parallel);

        group.cleanUp();

        // FIXME assertTrue(html.contains("Screenshots"), "Should contain the text Screenshots");
    }

    private class TestAutomation extends AbstractBaseAutomation {


        private TestAutomation() {}

        @Override
        public void checkPreconditions() throws PreconditionsException {

        }

        @Override
        public void run() {
            protocol.setTitle("SingleTest");
            protocol.info("FROM MAIN TEST!");
            protocol.setProtocolsPath("target/");
            protocol.hrLine();
            protocol.h1("Simple Protocol Test");
            protocol.paragraph("This will include hopefully every available element.");
            protocol.debug("For test purposes!");
            protocol.info("This is a info.");
            protocol.warn("And this is a warning!");
            protocol.warn("Another warning!");
            protocol.error("error = Beware, this one is a error!!!");
            protocol.h2("h2 = Asserts");
            protocol.assertSuccess("assertSuccess = Some will succeed.");
            protocol.assertFail("assertFail = Other will maybe fail!");
            protocol.h3("And there is more");
            protocol.preconditionFail("... like for example if a precondition fails ...");
            protocol.unexpectedTechnicalError("or even an unexpected technical error occurs");
            protocol.h4("Screenshots");
            protocol.screenshot("screenshot.png");
            protocol.screenshot("screenshot.png", "SCREENSHOT TITLE", "The same screenshot, but this time with a nice description");
        }

        @Override
        public void cleanUp() throws CleanUpException {

        }
    }

    @Snippet
    private class MiniSnippet extends AbstractBaseAutomation {

        @Override
        public void run() throws Exception {
            protocol.info("HELLO FROM SNIPPET! ... Should be included");
        }
    }

}
