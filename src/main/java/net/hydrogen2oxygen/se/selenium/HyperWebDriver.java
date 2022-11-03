package net.hydrogen2oxygen.se.selenium;

import net.hydrogen2oxygen.se.exceptions.CommandExecutionException;
import net.hydrogen2oxygen.se.exceptions.HyperWebDriverException;
import net.hydrogen2oxygen.se.protocol.Protocol;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public class HyperWebDriver {

    private Boolean closed = false;
    private WebDriver driver;
    private DriverTypes driverType;
    private Protocol protocol;

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public enum DriverTypes {
        LOCAL_CHROME, REMOTE_FIREFOX, REMOTE_CHROME
    }

    public HyperWebDriver(HyperWebDriver.DriverTypes driverType) throws HyperWebDriverException {
        init(driverType, null, null, false);
    }

    public HyperWebDriver(HyperWebDriver.DriverTypes driverType, String remoteUrl, String seleniumDriverDirectory, boolean headless) throws HyperWebDriverException {

        init(driverType, remoteUrl, seleniumDriverDirectory, headless);
    }

    private void init(HyperWebDriver.DriverTypes driverType, String remoteUrl, String seleniumDriverDirectory, boolean headless) throws HyperWebDriverException {

        this.driverType = driverType;

        if (seleniumDriverDirectory == null || seleniumDriverDirectory.trim().length() == 0) {
            seleniumDriverDirectory = "..";
        }

        if (System.getProperty("webdriver.chrome.driver") == null) {
            System.setProperty("webdriver.chrome.driver", seleniumDriverDirectory + "/chromedriver.exe");
        }

        if (DriverTypes.LOCAL_CHROME.equals(driverType)) {

            // TODO enably by option WebDriverManager.chromedriver().setup();
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setHeadless(headless);
            driver = new ChromeDriver(chromeOptions);

        } else if (DriverTypes.REMOTE_FIREFOX.equals(driverType)) {

            try {
                FirefoxOptions options = new FirefoxOptions();
                driver = new RemoteWebDriver(new URL(remoteUrl), options);
            } catch (MalformedURLException e) {
                throw new HyperWebDriverException("Remote firefox connection could not be established");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (DriverTypes.REMOTE_CHROME.equals(driverType)) {

            try {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--window-size=1400,600");
                driver = new RemoteWebDriver(new URL(remoteUrl), options);
            } catch (MalformedURLException e) {
                throw new HyperWebDriverException("Remote chrome connection could not be established");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            throw new HyperWebDriverException("Unknown option for browser or remote connection");
        }
    }

    public DriverTypes getDriverType() {
        return driverType;
    }

    public boolean isClosed() {
        return closed;
    }

    public String getScreenshotsPath() {
        return protocol.getScreenshotPath();
    }

    public HyperWebDriver openPage(String url) {
        protocol.debug("Open page " + url);
        driver.get(url);
        return this;
    }

    public HyperWebDriver sendReturnForElementByName(String name) {
        protocol.debug("Type ENTER for " + name);
        driver.findElement(By.name(name)).sendKeys(Keys.RETURN);
        return this;
    }

    public HyperWebDriver text(String id, String text) {
        protocol.debug("Set text '" + text + " for ID " + id);
        driver.findElement(By.id(id)).sendKeys(text);
        return this;
    }

    public HyperWebDriver textByName(String name, String text) {
        protocol.debug("Set text '" + text + "' for NAME " + name);
        driver.findElement(By.name(name)).sendKeys(text);
        return this;
    }

    public String getAttribute(String id, String attributeKey) {
        WebElement element = driver.findElement(By.id(id));

        if (element == null) {
            return "";
        }

        return element.getAttribute(attributeKey);
    }

    public HyperWebDriver click(String id) {
        protocol.debug("Click ID " + id);
        driver.findElement(By.id(id)).click();
        return this;
    }

    public HyperWebDriver click(By by) {
        protocol.debug("Click ID " + by);
        driver.findElement(by).click();
        return this;
    }

    public HyperWebDriver clickName(String name) {
        protocol.debug("Click NAME " + name);
        driver.findElement(By.name(name)).click();
        return this;
    }

    public HyperWebDriver clickTagContainingText(String tag, String text) throws CommandExecutionException {

        protocol.debug("Click Tag containing text '" + text + "'");
        List<WebElement> list = driver.findElements(By.tagName(tag));

        boolean clickPerformed = false;

        for (WebElement element : list) {

            String elementText = element.getText().trim();

            if (elementText.length() > 0 && elementText.contains(text)) {
                try {
                    element.click();
                } catch (ElementNotInteractableException e) {
                    // sometimes the clickable element is a button, but the text is inside a div inside the button
                    WebElement parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                            "return arguments[0].parentNode;", element);
                    parent.click();
                }
                clickPerformed = true;
                break;
            }
        }

        if (!clickPerformed) {
            String errorMsg = String.format("clickTagContainingText with tag = [%s] and text = [%s] was not successful!", tag, text);
            protocol.error(errorMsg);
            throw new CommandExecutionException(errorMsg);
        }

        return this;
    }

    public HyperWebDriver selectOption(String id, String optionText) {

        protocol.debug("Select option '" + optionText + "' in ID " + id);
        Select options = new Select(driver.findElement(By.id(id)));
        options.selectByVisibleText(optionText);

        return this;
    }

    public HyperWebDriver selectOption(String id, int index) {

        protocol.debug("Select option with index " + index + " in ID " + id);
        Select options = new Select(driver.findElement(By.id(id)));
        options.selectByIndex(index);

        return this;
    }

    public HyperWebDriver switchToFrame(String id) {
        protocol.debug("Switch to frame with ID " + id);
        driver.switchTo().frame(id);
        return this;
    }

    public HyperWebDriver switchToParentFrame() {
        protocol.debug("Switch to parent frame");
        driver.switchTo().parentFrame();
        return this;
    }

    public void close() {
        protocol.debug("Close connection to browser");
        closed = true;
        driver.close();
    }

    public void quit() {
        driver.quit();
    }

    public HyperWebDriver waitMillis(int millis) throws InterruptedException {
        protocol.debug("Wait milliseconds " + millis);
        Thread.sleep(millis);
        return this;
    }

    public HyperWebDriver waitForElement(String id, int seconds) {
        protocol.debug("Wait for element " + id + " for " + seconds + " seconds");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated((By
                .id(id))));

        return this;
    }

    public HyperWebDriver waitForTag(String tagName, int seconds) {
        protocol.debug("Wait for tag " + tagName + " for " + seconds + " seconds");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated((By
                .tagName(tagName))));

        return this;
    }

    public File screenshot() throws IOException {
        return screenshot(null, null);
    }

    public File screenshot(String title, String description) throws IOException {

        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        if (protocol.getScreenshotPath() != null) {
            File folder = new File(protocol.getScreenshotPath());

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File newFile = new File(protocol.getScreenshotPath() + file.getName());
            FileUtils.copyFile(file, newFile);

            if (title != null) {
                protocol.screenshot(title, description, file.getName());
            } else {
                protocol.screenshot(file.getName());
            }
            return newFile;
        }

        return file;
    }

    public String getHtml() {

        return driver.getPageSource();
    }

    public void clear(String id) {
        driver.findElement(By.id(id)).clear();
    }

    public void waitForJQuery() {

        protocol.debug("Wait for JQuery");
        waitForJavascript((JavascriptExecutor) driver, "return jQuery.active");
    }

    public void waitForJavascript(final String script) {

        protocol.debug("Wait for JavaScript");
        waitForJavascript((JavascriptExecutor) driver, script);
    }

    private void waitForJavascript(final JavascriptExecutor executor, final String script) {
        new FluentWait<JavascriptExecutor>(executor) {
            @Override
            protected RuntimeException timeoutException(
                    String message, Throwable lastException) {
                return new RuntimeException(message);
            }
        }.withTimeout(Duration.ofSeconds(10))
                .until(e -> {

                    Object result = executor.executeScript(script);

                    if (result instanceof Long) {
                        return (Long) result == 0;
                    }

                    if (result instanceof Boolean) {
                        return (Boolean) result;
                    }

                    return result != null;
                });
    }

    public Protocol getProtocol() {
        return protocol;
    }
}
