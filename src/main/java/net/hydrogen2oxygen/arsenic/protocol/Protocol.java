package net.hydrogen2oxygen.arsenic.protocol;

import j2html.tags.DomContent;
import j2html.tags.specialized.*;
import net.hydrogen2oxygen.arsenic.Environment;

import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;

/**
 * Every automation generates a protocol
 */
public class Protocol {

    private final List<ProtocolEntry> protocolEntryList = new ArrayList<>();

    private String title;
    private String screenshotPath;
    private String protocolsPath;
    private Environment env;

    public enum ProtocolType {
        H1, H2, H3, H4, HR, PARAGRAPH, DEBUG, INFO, WARNING, ERROR, PRECONDITION_FAIL, SCREENSHOT,
        SCREENSHOT_WITH_DESCRIPTION, ASSERT_SUCCESS, ASSERT_FAIL, UNEXPECTED_TECHNICAL_ERROR,
        SKIP
    }

    public enum ProtocolResult {
        SUCCESS, FAIL, PRECONDITION_FAIL, TECHNICAL_ERROR, SKIPPED, UNKNOWN
    }

    public void h1(String title) {
        add(ProtocolType.H1, title);
    }

    public void h2(String title) {
        add(ProtocolType.H2, title);
    }

    public void h3(String title) {
        add(ProtocolType.H3, title);
    }

    public void h4(String title) {
        add(ProtocolType.H4, title);
    }

    public void paragraph(String text) {
        add(ProtocolType.PARAGRAPH, text);
    }

    /**
     * Use "debug" for every technical protocol. Debug level can be switched off globally.
     *
     * @param message to protocol
     */
    public void debug(String message) {
        if (env != null && "TRUE".equals(env.get("DEBUG"))) {
            add(ProtocolType.DEBUG, message);
        }
    }

    public void hrLine() {
        add(ProtocolType.HR);
    }

    public void info(String message) {
        add(ProtocolType.INFO, message);
    }

    public void warn(String message) {
        add(ProtocolType.WARNING, message);
    }

    public void error(String message) {
        add(ProtocolType.ERROR, message);
    }

    public void skip(String message) {
        add(ProtocolType.SKIP, message);
    }

    public void preconditionFail(String message) {
        add(ProtocolType.PRECONDITION_FAIL, message);
    }

    public void screenshot(String imageId) {
        add(ProtocolType.SCREENSHOT, imageId);
    }

    public void screenshot(String imageId, String title, String description) {
        add(ProtocolType.SCREENSHOT_WITH_DESCRIPTION, title + "|" + description + "|" + imageId);
    }

    public void assertSuccess(String message) {
        add(ProtocolType.ASSERT_SUCCESS, message);
    }

    public void assertFail(String message) {
        add(ProtocolType.ASSERT_FAIL, message);
    }

    public void unexpectedTechnicalError(String message) {
        add(ProtocolType.UNEXPECTED_TECHNICAL_ERROR, message);
    }

    private void add(ProtocolType protocolType, String data) {
        protocolEntryList.add(new ProtocolEntry(protocolType, data));
    }

    private void add(ProtocolType protocolType) {
        protocolEntryList.add(new ProtocolEntry(protocolType, null));
    }

    public static class ProtocolEntry {

        private final ProtocolType protocolType;
        private String data = "";

        public ProtocolEntry(ProtocolType protocolType, String data) {
            this.protocolType = protocolType;
            this.data = data;
        }

        public DomContent getDomContent() {

            switch (this.protocolType) {
                case DEBUG:
                    return new DivTag().with(span(data).withClass("debug"));
                case PARAGRAPH:
                    return new PTag().withText(data);
                case INFO:
                    return new DivTag().with(span(data).withClasses("badge", "bg-primary"));
                case WARNING:
                    return new DivTag().with(span(data).withClasses("badge", "bg-warning", "text-dark"));
                case ERROR:
                case PRECONDITION_FAIL:
                case UNEXPECTED_TECHNICAL_ERROR:
                    return new DivTag().with(span(data).withClasses("badge", "bg-danger"));
                case HR:
                    return new DivTag().with(hr());
                case H1:
                    return new H1Tag().withText(data);
                case H2:
                    return new H2Tag().withText(data);
                case H3:
                    return new H3Tag().withText(data);
                case H4:
                    return new H4Tag().withText(data);
                case SCREENSHOT:
                    return new PreTag().with(a().with(img().withSrc(data).withHeight("400px")).withHref(data).withTarget(data));
                case SCREENSHOT_WITH_DESCRIPTION:
                    return createScreenShotCard(data);
                case ASSERT_SUCCESS:
                    return new DivTag().with(span(data).withClass("SUCCESS"));
                case ASSERT_FAIL:
                    return new DivTag().with(span(data).withClass("FAIL"));
                default:
                    return new DivTag().withText(protocolType.name() + " - " + data);
            }
        }

        private DomContent createScreenShotCard(String data) {

            if (data == null) {
                return new PreTag().withText("no data delivered for screenshot");
            }

            String title = null;
            String text = null;
            String url = data;

            if (data.contains("|")) {

                String parts[] = data.split("\\|");

                if (parts.length == 2) {
                    title = parts[0];
                    url = parts[1];
                } else if (parts.length == 3) {
                    title = parts[0];
                    text = parts[1];
                    url = parts[2];
                }
            }

            return new DivTag()
                    .withClass("card")
                    .withStyle("width: 18rem")
                    .with(img().withSrc(url).withClass("card-img-top"),
                            div(h5(title).withClass("card-title"),
                                    p(text).withClass("card-text")).withClass("card-body"));
        }
    }

    public ProtocolResult getProtocolResult() {

        boolean assertSuccess = false;

        for (ProtocolEntry entry : protocolEntryList) {

            switch (entry.protocolType) {
                case UNEXPECTED_TECHNICAL_ERROR:
                    return ProtocolResult.TECHNICAL_ERROR;
                case ASSERT_FAIL:
                    return ProtocolResult.FAIL;
                case PRECONDITION_FAIL:
                    return ProtocolResult.PRECONDITION_FAIL;
                case SKIP:
                    return ProtocolResult.SKIPPED;
                case ASSERT_SUCCESS:
                    assertSuccess = true;
                    break;
                default:
                    // nothing (all other types are no "result types")
            }
        }

        if (assertSuccess) return ProtocolResult.SUCCESS;

        return ProtocolResult.UNKNOWN;
    }

    public List<ProtocolEntry> getProtocolEntryList() {
        return protocolEntryList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    public void setScreenshotPath(String screenshotPath) {
        this.screenshotPath = screenshotPath;
    }

    public String getProtocolsPath() {
        return protocolsPath;
    }

    public void setProtocolsPath(String protocolsPath) {
        this.protocolsPath = protocolsPath;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }
}
