package net.hydrogen2oxygen.arsenic;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hydrogen2oxygen.arsenic.exceptions.EnvironmentException;
import net.hydrogen2oxygen.arsenic.exceptions.HyperWebDriverException;
import net.hydrogen2oxygen.arsenic.selenium.HyperWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 1) Download the proper Browser Driver (chromedriver.exe for example) or set up a docker module for selenium
 * 2) set the environment with java or system properties (example -Denvironment=exampleEnvironment.json)
 * 3) start your test with se.run(MyTest.java);
 */
public class Se {

    public static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
    public static final String WEBDRIVER_TYPE = "webdriver.type";
    public static final String SCREENSHOTS_PATH = "screenshots.path";
    public static final String PROTOCOLS_PATH = "protocols.path";
    public static final String HEADLESS = "headless";
    public static final String ENVIRONMENT = "environment";
    private static final Logger logger = LogManager.getLogger(Se.class);
    private Environment environment;
    private HyperWebDriver webDriver;

    private Se(Environment env, HyperWebDriver.DriverTypes webDriverType) throws HyperWebDriverException, EnvironmentException, IOException {
        init(env, webDriverType);
    }

    private void init(Environment env, HyperWebDriver.DriverTypes webDriverType) throws HyperWebDriverException, EnvironmentException, IOException {

        if (env == null) {
            environment = loadEnvironment(System.getProperty(ENVIRONMENT));
        } else {
            environment = env;
        }

        if (webDriverType == null && environment.get(WEBDRIVER_TYPE) != null) {
            webDriverType = HyperWebDriver.DriverTypes.valueOf(environment.get(WEBDRIVER_TYPE));
        }

        if (webDriverType == null) {
            webDriverType = HyperWebDriver.DriverTypes.LOCAL_CHROME;
        }

        if (environment.get(WEBDRIVER_CHROME_DRIVER) != null) {
            System.setProperty(WEBDRIVER_CHROME_DRIVER, environment.get(WEBDRIVER_CHROME_DRIVER));
        }

        try {
            webDriver = new HyperWebDriver(webDriverType, environment.get("baseUrl"), null, environment.getBoolean(Se.HEADLESS));
        } catch (IllegalStateException e) {
            logger.error(e);
            throw new HyperWebDriverException("Check your driver configuration please!", e);
        }
    }

    public static Environment loadEnvironment() throws EnvironmentException, IOException {
        return loadEnvironment(System.getProperty(ENVIRONMENT));
    }

    public static Environment loadEnvironment(String environmentFileString) throws EnvironmentException, IOException {

        if (environmentFileString == null || environmentFileString.trim().length() == 0) {
            throw new EnvironmentException("No environment provided. You cannot switch an environment without proper configuration. But you can still work with the standard automation.");
        }

        logger.info("Loading environment {}", environmentFileString);

        InputStream environmentFileInputStream = null;
        File environmentFile = new File(environmentFileString);

        // priority to external files
        if (environmentFile.exists()) {
            environmentFileInputStream = new FileInputStream(environmentFile);
        } else {
            environmentFileInputStream = Se.class.getClassLoader().getResourceAsStream(environmentFileString);
        }

        if (environmentFileInputStream == null) {
            throw new EnvironmentException("Unable to load environment: " + environmentFileString);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(environmentFileInputStream, Environment.class);
    }

    public static Se getInstance() throws HyperWebDriverException, EnvironmentException, IOException {

        return new Se(null, null);
    }

    public static Se getInstance(Environment env, HyperWebDriver.DriverTypes webDriverType) throws HyperWebDriverException, EnvironmentException, IOException {

        return new Se(env, webDriverType);
    }

    /**
     * True if the class contains a snippet annotation
     *
     * @param object to check
     * @return true if class of object has annotation {@link Snippet}
     */
    public static boolean isSnippet(Object object) {

        if (object == null) {
            return false;
        }

        return object.getClass().isAnnotationPresent(Snippet.class);
    }

    public void setEnvironment(String path) throws IOException, EnvironmentException {
        this.environment = loadEnvironment(path);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void run(IAutomation automation) {
        try {
            logger.info("Running automation {}", automation.getClass().getSimpleName());
            automation.setSe(this);
            automation.checkPreconditions();
            automation.run();
            automation.cleanUp();
        } catch (Exception pe) {
            logger.error(pe);
        }
    }

    public HyperWebDriver getWebDriver() {
        return webDriver;
    }
}
