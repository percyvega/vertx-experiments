package com.percyvega.verticles_01_http.group_02;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class V1_HtmlFilesUnderWebroot extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V1_HtmlFilesUnderWebroot.class);

    public static void main(String[] args) {
        LOGGER.info("*********************************************************** Hello from " + V1_HtmlFilesUnderWebroot.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_HtmlFilesUnderWebroot());
    }

    @Override
    public void start() {
        LOGGER.info("*********************************************************** Verticle App Started ***********************************************************");

        Router router = Router.router(vertx);

        router
                .route()
                .handler(StaticHandler.create().setCachingEnabled(false));

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    @Override
    public void stop() {
        LOGGER.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
