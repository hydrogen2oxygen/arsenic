package net.hydrogen2oxygen.arsenic.automations;

import net.hydrogen2oxygen.arsenic.AbstractBaseAutomation;
import net.hydrogen2oxygen.arsenic.exceptions.CleanUpException;
import net.hydrogen2oxygen.arsenic.exceptions.PreconditionsException;
import net.hydrogen2oxygen.arsenic.exceptions.WrappedCheckedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.opentest4j.AssertionFailedError;

import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenGithubSearchSelenium extends AbstractBaseAutomation {

    private static Logger logger = LogManager.getLogger(OpenGithubSearchSelenium.class);

    @Override
    public void checkPreconditions() throws PreconditionsException {
        try {
            assertNotNull(wd);
            assertTrue(ping("https://github.com"));
        } catch (AssertionFailedError e) {
            throw new PreconditionsException("Precondition failed", e);
        }
    }

    @Override
    public void run() {

        try {
            wd.openPage("https://github.com/hydrogen2oxygen/arsenic")
                    .waitMillis(1000);
            wd.getProtocol().h1("Search in Github");
            wd.textByName("q", "Selenium")
                    .sendReturnForElementByName("q")
                    .screenshot();
        } catch (Exception e) {
            throw new WrappedCheckedException(e);
        }

        String html = wd.getHtml();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("a");
        ListIterator<Element> iter = elements.listIterator();

        while (iter.hasNext()) {
            Element element = iter.next();
            logger.debug(element.text());
        }
    }

    @Override
    public void cleanUp() throws CleanUpException {
        //wd.close(); ... don't do this inside a snippet or inside a automation intended to run inside a group
    }
}
