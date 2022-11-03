package net.hydrogen2oxygen.se.protocol;

import net.hydrogen2oxygen.se.*;
import net.hydrogen2oxygen.se.exceptions.CleanUpException;
import net.hydrogen2oxygen.se.exceptions.PreconditionsException;
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

        System.setProperty("environment", "/exampleEnvironment.json");
        Environment environment = Se.loadEnvironment();

        TestAutomation testAutomation = new TestAutomation();
        Protocol protocol = testAutomation.getProtocol();
        protocol.setTitle("SingleTest");
        protocol.setProtocolsPath("target/");
        protocol.h1("Simple Protocol Test");
        protocol.paragraph("This will include hopefully every available element.");
        protocol.debug("For test purposes!");
        protocol.info("This is a info.");
        protocol.warn("And this is a warning!");
        protocol.error("Beware, this one is a error!!!");
        protocol.h2("Asserts");
        protocol.assertSuccess("Some will succeed.");
        protocol.assertFail("Other will maybe fail!");
        protocol.h3("And there is more");
        protocol.preconditionFail("... like for example if a precondition fails ...");
        protocol.unexpectedTechnicalError("or even an unexpected technical error occurs");
        protocol.h4("Screenshots");
        protocol.screenshot("screenshot.png");
        protocol.screenshot("screenshot.png", "SCREENSHOT TITLE", "The same screenshot, but this time with a nice description");

        Group group = new Group("TestGroup", environment);
        Parallel parallel = new Parallel("ParallelTest", environment);

        group.add(testAutomation);
        parallel.add(group);

        ProtocolGeneratorHtml generatorHtml = new ProtocolGeneratorHtml();
        String html = generatorHtml.generateHtml(parallel);

        group.cleanUp();

        // FIXME assertTrue(html.contains("Screenshots"), "Should contain the text Screenshots");
    }

    private class TestAutomation extends AbstractBaseAutomation {


        @Override
        public void checkPreconditions() throws PreconditionsException {

        }

        @Override
        public void run() {

        }

        @Override
        public void cleanUp() throws CleanUpException {

        }
    }

}
