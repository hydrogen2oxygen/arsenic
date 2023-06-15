package net.hydrogen2oxygen.arsenic;

import net.hydrogen2oxygen.arsenic.exceptions.CleanUpException;
import net.hydrogen2oxygen.arsenic.exceptions.ParallelExecutionException;
import net.hydrogen2oxygen.arsenic.exceptions.WrappedCheckedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Parallel extends AbstractBaseAutomation {

    private static final Logger logger = LogManager.getLogger(Parallel.class);
    private final List<IAutomation> automationList = new ArrayList<>();

    public Parallel(String parallelName, Environment env) {
        this.env = env;
        initProtocol(parallelName);
    }

    @Override
    public void run() {

        ExecutorService es = Executors.newFixedThreadPool(env.getInt("nThreads"));

        for (IAutomation automation : automationList) {
            es.execute(() -> {
                // TODO evaluate to instance the WebDriver here instead inside the Group / Se class
                // https://www.baeldung.com/java-threadlocal
                if (automation instanceof Group) {
                    Group group = (Group) automation;
                    try {
                        group.checkPreconditions();
                        group.run();
                        group.cleanUp();
                    } catch (Exception e) {
                        logger.error(
                                () -> new ParameterizedMessage("Error during parallel run of group: {}", group.getGroupName()
                                ), e);
                    }
                } else {

                    try {
                        Se se = Se.getInstance();
                        se.run(automation);
                        se.getWebDriver().close();
                    } catch (Exception e) {
                        logger.error(
                                () -> new ParameterizedMessage("{} during parallel run of automation: {}"
                                        , e.getClass().getSimpleName(), automation.getClass().getSimpleName()
                                ), e
                        );
                    }
                }
            });
        }

        es.shutdown();
        boolean finished = false;
        try {
            Integer parallelTimeOutMinutes = env.getInt("parallel.timeout.minutes");
            if (parallelTimeOutMinutes == null) {
                parallelTimeOutMinutes = 15;
                logger.info("parallel.timeout.minutes not set, using default of 15 minutes");
            }
            finished = es.awaitTermination(parallelTimeOutMinutes, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            es.shutdownNow();
            throw new WrappedCheckedException("Shutdown forced", e);
        }

        if (!finished) {
            throw new ParallelExecutionException("Parallel execution not finished after " + env.getInt("parallel.timeout.minutes") + " minutes yet!");
        }
    }

    @Override
    public void cleanUp() throws CleanUpException {
        wd.close();
    }

    /**
     * Add a group or single automation which shall run in a parallel thread
     */
    public Parallel add(IAutomation ... automations) {
        Arrays.stream(automations).sequential().forEach( a -> automationList.add(a));
        return this;
    }

    /**
     * Add a group or single automation which shall run in a parallel thread
     */
    public Parallel add(IAutomation automation) {
        automationList.add(automation);
        return this;
    }

    public List<IAutomation> getAutomationList() {
        return automationList;
    }
}
