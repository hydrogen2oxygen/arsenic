package net.hydrogen2oxygen.se;

import net.hydrogen2oxygen.se.automations.OpenGithubSearchElectron;
import net.hydrogen2oxygen.se.automations.OpenGithubSearchHydrogen2oxygen;
import net.hydrogen2oxygen.se.automations.OpenGithubSearchSelenium;
import net.hydrogen2oxygen.se.automations.OpenGithubSearchSpringBoot;
import net.hydrogen2oxygen.se.exceptions.EnvironmentException;
import net.hydrogen2oxygen.se.exceptions.HyperWebDriverException;
import net.hydrogen2oxygen.se.protocol.ProtocolGeneratorHtml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

class SeTest {

    private static final Logger logger = LogManager.getLogger(SeTest.class);

    @Test
    void testSingleSnippet() {

        try {
            Se se = Se.getInstance();
            se.setEnvironment("exampleEnvironment.json");

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
            Environment environment = Se.loadEnvironment();
            // add a group
            Group group1 = new Group("GitHub1", environment);
            group1.add(new OpenGithubSearchSelenium());
            group1.add(new OpenGithubSearchHydrogen2oxygen());

            // and a second group
            Group group2 = new Group("GitHub2", environment);
            group2.add(new OpenGithubSearchElectron());
            group2.add(new OpenGithubSearchSpringBoot());

            // run group 1 and 2 in parallel
            Parallel parallel = new Parallel("Parallel Selenium Run, prove of concept", environment);
            parallel.add(group1);
            parallel.add(group2);
            parallel.run();

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
