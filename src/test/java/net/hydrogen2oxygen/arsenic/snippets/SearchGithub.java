package net.hydrogen2oxygen.arsenic.snippets;

import net.hydrogen2oxygen.arsenic.AbstractBaseAutomation;
import net.hydrogen2oxygen.arsenic.Snippet;

@Snippet
public class SearchGithub extends AbstractBaseAutomation {

    private final String queryString;

    public SearchGithub(String queryString) {
        super();
        this.queryString = queryString;
    }

    @Override
    public void run() {
        wd.textByName("q", queryString)
                .sendReturnForElementByName("q")
                .screenshot();
        protocol.info("Search Github done!");
    }
}
