package com.percyvega.verticles_01_http.group_02;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class V2_HtmlFileWithPlaceholders extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2_HtmlFileWithPlaceholders.class);

    public static void main(String[] args) {
        LOGGER.info("*********************************************************** Hello from " + V2_HtmlFileWithPlaceholders.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V2_HtmlFileWithPlaceholders());
    }

    @Override
    public void start() {
        LOGGER.info("*********************************************************** Verticle App Started ***********************************************************");

        Router router = Router.router(vertx);

        router
                .get("/yo.html")
                .handler(this::routingContextHandler);

        router
                .route()
                .handler(StaticHandler.create().setCachingEnabled(false)); // StaticHandler looks inside resources/webroot

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    private void routingContextHandler(RoutingContext routingContext) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("webroot/yo.html").getFile());

        String updatedHtml = "";
        try {
            StringBuilder result = new StringBuilder();
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                line = line.replace("{name}", "Percy Vega");
                result.append(line).append("\n");
            }

            scanner.close();
            updatedHtml = result.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(updatedHtml);
    }

    @Override
    public void stop() {
        LOGGER.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
