# Arsenic

Fast browser automations for tests or other purposes

## Selenium as driver

Arsenic use Selenium as driver for browser automation.

## Features
* Fast setup of browser automations
* Parallel execution of browser instances
* Automatic screenshot and protocol generation
* Easy to use

## Dependency for Maven

```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependency>
        <groupId>com.github.hydrogen2oxygen</groupId>
        <artifactId>Se</artifactId>
        <version>master-SNAPSHOT</version>
    </dependency>
```

## Usage
In order to use Arsenic you need to create an environment file, for example `exampleEnvironment.json` (put it inside your resources folder):
```json
{
  "data": {
    "headless": "false",
    "webDriverEnabled": "false",
    "nThreads": "8",
    "baseUrl": "https://blabla.thisandthat.com",
    "ping.timeout.milliseconds": "5000",
    "parallel.timeout.minutes": "15",
    "userName": "John",
    "screenshots.path": "target/protocols/",
    "protocols.path": "target/protocols/"
  },
  "name": "simple"
}
```

### Basic example, single execution

Note: You need to create your own snippet, for example `OpenGithubSearchSelenium` (see [OpenGithubSearchSelenium.java](src/test/java/com/github/hydrogen2oxygen/arsenic/snippets/OpenGithubSearchSelenium.java) for an example).
```java
import net.hydrogen2oxygen.arsenic.automations.*;
import net.hydrogen2oxygen.arsenic.exceptions.*;
import net.hydrogen2oxygen.arsenic.protocol.ProtocolGeneratorHtml;
import net.hydrogen2oxygen.arsenic.selenium.HyperWebDriver;
import org.apache.logging.log4j.*;
import org.junit.jupiter.api.Test;

public class Example {

    private static final Logger logger = LogManager.getLogger(Example.class);
    
    public static void main(String[] args) {
        try {

            Se se = Se.getInstance(Se.loadEnvironment("exampleEnvironment.json"), HyperWebDriver.DriverTypes.LOCAL_CHROME);

            // let's use a snippet
            OpenGithubSearchSelenium openGithubSearchSelenium = new OpenGithubSearchSelenium();
            openGithubSearchSelenium.setSe(se);
            se.run(openGithubSearchSelenium);
            se.getWebDriver().close();

            ProtocolGeneratorHtml protocolGeneratorHtml = new ProtocolGeneratorHtml();
            protocolGeneratorHtml.generateHtml(openGithubSearchSelenium);

        } catch (HyperWebDriverException e) {
            logger.error("Check your driver configuration please!", e);
        } catch (Exception e) {
            logger.error("Unexpected error occured", e);
        }
    }
}
```

## Parallel execution

Arsenic supports parallel execution of browser instances. You can create multiple driver instances and run them in separate threads.

The threads are managed by Arsenic, you just need to define the groups of snippets you want to run in parallel.

```java
import net.hydrogen2oxygen.arsenic.automations.*;
import net.hydrogen2oxygen.arsenic.exceptions.*;
import net.hydrogen2oxygen.arsenic.protocol.ProtocolGeneratorHtml;
import net.hydrogen2oxygen.arsenic.selenium.HyperWebDriver;
import org.apache.logging.log4j.*;
import org.junit.jupiter.api.Test;

public class ParallelExample {

    private static final Logger logger = LogManager.getLogger(ParallelExample.class);
    
    public static void main(String[] args) throws InterruptedException {
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
```
