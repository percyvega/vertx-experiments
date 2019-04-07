package com.percyvega.v1_http.g2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class V1_HtmlFilesUnderWebroot extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(V1_HtmlFilesUnderWebroot.class);

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + V1_HtmlFilesUnderWebroot.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_HtmlFilesUnderWebroot());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

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
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
