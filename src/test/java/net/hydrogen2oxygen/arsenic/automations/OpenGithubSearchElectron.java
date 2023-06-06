package net.hydrogen2oxygen.arsenic.automations;

import net.hydrogen2oxygen.arsenic.AbstractBaseAutomation;
import net.hydrogen2oxygen.arsenic.exceptions.CleanUpException;
import net.hydrogen2oxygen.arsenic.exceptions.PreconditionsException;
import net.hydrogen2oxygen.arsenic.exceptions.WrappedCheckedException;
import net.hydrogen2oxygen.arsenic.snippets.SearchGithub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenGithubSearchElectron extends AbstractBaseAutomation {

    private static final Logger logger = LogManager.getLogger(OpenGithubSearchElectron.class);

    @Override
    public void checkPreconditions() throws PreconditionsException {
        assertNotNull(wd);
        assertTrue(ping("https://github.com"));
    }

    @Override
    public void run() {

        try {
            wd.openPage("https://github.com/hydrogen2oxygen/arsenic")
                    .waitMillis(1000);
        } catch (InterruptedException e) {
            throw new WrappedCheckedException(e);
        }

        snippet(new SearchGithub("electron"));

        String html = wd.getHtml();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("a");

        for (Element element : elements) {
            logger.debug(element.text());
        }
    }

    @Override
    public void cleanUp() throws CleanUpException {
        //wd.close(); ... don't do this inside a snippet or inside a automation intended to run inside a group
    }
}
