package net.hydrogen2oxygen.arsenic;

import net.hydrogen2oxygen.arsenic.exceptions.CleanUpException;
import net.hydrogen2oxygen.arsenic.exceptions.PreconditionsException;
import net.hydrogen2oxygen.arsenic.exceptions.SnippetException;
import net.hydrogen2oxygen.arsenic.protocol.Protocol;
import net.hydrogen2oxygen.arsenic.selenium.HyperWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Abstract Base Automation providing basics for all automation classes
 */
public abstract class AbstractBaseAutomation implements IAutomation {

    public static final String PING_TIMEOUT = "ping.timeout.milliseconds";
    public static final int TIME_OUT = 5000;
    private static final Logger logger = LogManager.getLogger(AbstractBaseAutomation.class);

    protected Se se;
    protected HyperWebDriver wd;
    protected Environment env;
    protected final Protocol protocol;

    protected AbstractBaseAutomation(Protocol protocol) {
        this.protocol = protocol;
    }

    protected AbstractBaseAutomation() {
        this.protocol = new Protocol();
    }

    @Override
    public void setSe(Se se) {
        this.se = se;
        this.env = se.getEnvironment();
        this.wd = se.getWebDriver();

        protocol.setEnv(env);
        protocol.setTitle(this.getClass().getSimpleName());
        protocol.setProtocolsPath(env.get(Se.PROTOCOLS_PATH));
        protocol.setScreenshotPath(env.get(Se.SCREENSHOTS_PATH));

        this.wd.setProtocol(protocol);
    }

    /**
     * Runs a snippet
     *
     * @param automation snippet to run
     */
    public void snippet(IAutomation automation) {
        if (!Se.isSnippet(automation)) {
            throw new SnippetException("The class " + automation.getClass().getName() + " is not a Snippet! You need to annotate snippets!");
        }
        automation.setSe(se);
        automation.checkPreconditions();
        try {
            automation.run();
        } catch (Exception e) {
            automation.getProtocol().error(e.getMessage());
        }
        automation.cleanUp();
        getProtocol().getProtocolEntryList().addAll(automation.getProtocol().getProtocolEntryList());
    }

    /**
     * Performs a ping to a host
     *
     * @param url
     * @return true if success
     */
    public boolean ping(String url) {

        Integer timeOut = getTimeOut();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeOut);
            connection.setReadTimeout(timeOut);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (MalformedURLException e) {
            protocol.warn("PING - Host " + url + " is unknown!");
            return false;
        } catch (IOException e) {
            logger.warn("PING - Host {} unreachable, TIMEOUT after {} seconds !", url, timeOut);
            return false;
        }
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    public void initProtocol(String title) {
        protocol.setTitle(title);
        protocol.setProtocolsPath(env.get(Se.PROTOCOLS_PATH));
        protocol.setScreenshotPath(env.get(Se.SCREENSHOTS_PATH));
    }

    private Integer getTimeOut() {

        Integer timeOut = env.getInt(PING_TIMEOUT);

        if (timeOut == null) {
            logger.debug(PING_TIMEOUT + " value not set, using default value of {} milliseconds!", TIME_OUT);
            timeOut = TIME_OUT;
        }

        return timeOut;
    }

    @Override
    public void checkPreconditions() throws PreconditionsException {
        // implement one in your class, this is the abstract class
    }

    @Override
    public void cleanUp() throws CleanUpException {
        // implement one in your class, this is the abstract class
    }

}
