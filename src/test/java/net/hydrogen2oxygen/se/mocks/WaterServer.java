package net.hydrogen2oxygen.se.mocks;

import io.javalin.Javalin;

/**
 * Mocks a complete web application that helps to cover each test in Se
 */
public class WaterServer {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.get("/", ctx -> ctx.result("Hello World"));
    }
}
