package net.hydrogen2oxygen.arsenic;

import net.hydrogen2oxygen.arsenic.automations.OpenGithubSearchElectron;
import net.hydrogen2oxygen.arsenic.automations.OpenGithubSearchHydrogen2oxygen;
import net.hydrogen2oxygen.arsenic.automations.OpenGithubSearchSelenium;
import net.hydrogen2oxygen.arsenic.automations.OpenGithubSearchSpringBoot;
import net.hydrogen2oxygen.arsenic.exceptions.EnvironmentException;
import net.hydrogen2oxygen.arsenic.exceptions.HyperWebDriverException;
import net.hydrogen2oxygen.arsenic.protocol.ProtocolGeneratorHtml;
import net.hydrogen2oxygen.arsenic.selenium.HyperWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

class SeTest {

    private static final Logger logger = LogManager.getLogger(SeTest.class);

    @Test
    void testSingleSnippet() {

        try {

            Se se = Se.getInstance(Se.loadEnvironment("exampleEnvironment.json"), HyperWebDriver.DriverTypes.LOCAL_CHROME);

            // let's use a snippet
            OpenGithubSearchSelenium openGithubSearchSelenium = new OpenGithubSearchSelenium();
            openGithubSearchSelenium.setSe(se);
            se.run(openGithubSearchSelenium);
            se.getWebDriver().close();

        } catch (HyperWebDriverException e) {
            logger.error("Check your driver configuration please!", e);
        } catch (Exception e) {
            logger.error("Unexpected error occured", e);
        }
    }

    @Test
    void testParallel() throws Exception {

        try {
            // load the environment
            Environment environment = Se.loadEnvironment("exampleEnvironment.json");
            // add a group
            Group group1 = new Group("GitHub1", environment);
            group1.add(new OpenGithubSearchSelenium()).add(new OpenGithubSearchHydrogen2oxygen());

            // and a second group
            Group group2 = new Group("GitHub2", environment);
            group2.add(new OpenGithubSearchElectron()).add(new OpenGithubSearchSpringBoot());

            // run group 1 and 2 in parallel
            Parallel parallel = new Parallel("Parallel Selenium Run, prove of concept", environment);
            parallel.add(group1, group2).run();

            ProtocolGeneratorHtml protocolGeneratorHtml = new ProtocolGeneratorHtml();
            protocolGeneratorHtml.generateHtml(parallel);

        } catch (HyperWebDriverException e) {
            logger.error("Check your driver configuration please!", e);
        } catch (EnvironmentException e) {
            logger.error("Test not runnable because of missing environment variable!", e);
        } catch (Exception e) {
            logger.error("Unexpected error occured", e);
            throw e;
        }
    }
}
