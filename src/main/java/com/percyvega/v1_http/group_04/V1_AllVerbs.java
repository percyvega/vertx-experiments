package com.percyvega.v1_http.group_04;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class V1_AllVerbs extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(V1_AllVerbs.class);

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + V1_AllVerbs.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_AllVerbs());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        Router router = Router.router(vertx);

        router
                .mountSubRouter("/api/", ProductResources.getSubRouterRouter(vertx));

        router
                .route()
                .handler(StaticHandler.create().setCachingEnabled(false));

        int port = 8080;
        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(port, asyncResult -> {
                    if(asyncResult.succeeded()) {
                        log.info("HTTP server running on port " + port);
                    } else {
                        log.info("Could not start an HTTP server", asyncResult.cause());
                    }
                });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}

