package net.hydrogen2oxygen.arsenic;

import net.hydrogen2oxygen.arsenic.exceptions.CleanUpException;
import net.hydrogen2oxygen.arsenic.exceptions.EnvironmentException;
import net.hydrogen2oxygen.arsenic.exceptions.HyperWebDriverException;
import net.hydrogen2oxygen.arsenic.exceptions.PreconditionsException;
import net.hydrogen2oxygen.arsenic.selenium.HyperWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Group extends AbstractBaseAutomation {

    private static final Logger logger = LogManager.getLogger(Group.class);
    private final String groupName;
    private final List<IAutomation> automationList = new ArrayList<>();

    /**
     * Don't use this constructor for parallel automation, only for simple grouping.
     *
     * @param se        used {@link Se} instance
     * @param groupName name of thread group
     */
    public Group(Se se, String groupName) {
        this.setSe(se);
        this.groupName = groupName;
        initProtocol(groupName);
    }

    /**
     * Use this constructor for parallel automation, because it will create a separate instance of selenium webDriver
     *
     * @param environment use {@link Environment} instance
     * @param groupName   name of thread group
     * @throws HyperWebDriverException see {@link Se#getInstance(Environment, HyperWebDriver.DriverTypes)}
     * @throws IOException             see {@link Se#getInstance(Environment, HyperWebDriver.DriverTypes)}
     * @throws EnvironmentException    see {@link Se#getInstance(Environment, HyperWebDriver.DriverTypes)}
     */
    public Group(String groupName, Environment environment) throws HyperWebDriverException, IOException, EnvironmentException {
        this.se = Se.getInstance(environment, null);
        this.se.setEnvironment(environment);
        setSe(se);
        this.groupName = groupName;
        initProtocol(groupName);
    }

    /**
     * The automation added to a group will receive all environments from the group
     *
     * @param automation to run
     */
    public Group add(IAutomation automation) {
        automation.setSe(se);
        automationList.add(automation);
        return this;
    }

    @Override
    public void run() {

        logger.info("Running group automation for {}", groupName);

        for (IAutomation automation : automationList) {

            logger.info("Running automation {}", automation.getClass().getSimpleName());

            try {
                automation.checkPreconditions();
                automation.run();
                automation.cleanUp();
            } catch (PreconditionsException e) {
                logger.error("PreconditionsException for automation: " + automation.getClass().getSimpleName(), e);
                automation.getProtocol().preconditionFail(getStackTraceAsString(e));
            } catch (Exception e) {
                logger.error(e);
                automation.getProtocol().unexpectedTechnicalError(getStackTraceAsString(e));
            }
        }
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString();
        return sStackTrace;
    }

    @Override
    public void cleanUp() throws CleanUpException {
        // finally, a group always close the driver, ALWAYS
        logger.info("Closing driver for group {}", groupName);
        wd.close();
    }

    public String getGroupName() {
        return groupName;
    }

    public List<IAutomation> getAutomationList() {
        return automationList;
    }
}
