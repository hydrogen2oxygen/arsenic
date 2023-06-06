package net.hydrogen2oxygen.arsenic.snippets;

import net.hydrogen2oxygen.arsenic.AbstractBaseAutomation;
import net.hydrogen2oxygen.arsenic.Snippet;
import net.hydrogen2oxygen.arsenic.exceptions.WrappedCheckedException;

import java.io.IOException;

@Snippet
public class SearchGithub extends AbstractBaseAutomation {

    private final String queryString;

    public SearchGithub(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public void run() {
        try {
            wd.textByName("q", queryString)
                    .sendReturnForElementByName("q")
                    .screenshot();
        } catch (IOException e) {
            throw new WrappedCheckedException(e);
        }
    }
}
