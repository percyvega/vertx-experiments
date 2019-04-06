package com.percyvega.verticles_01_http.group_03;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Arrays;

public class V1_ArtificialUrl extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(V1_ArtificialUrl.class);

    public static void main(String[] args) {
        log.info("*********************************************************** Hello from " + V1_ArtificialUrl.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_ArtificialUrl());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        Router router = Router.router(vertx);

        router
                .get("/api/v1/products.html")
                .handler(this::getAllProducts);

        router
                .route()
                .handler(StaticHandler.create().setCachingEnabled(false));

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    private void getAllProducts(RoutingContext routingContext) {
        Product product1 = new Product(152, "My item 152");
        Product product2 = new Product(1445, "My item 1445");

        routingContext
                .response()
                .setStatusCode(200)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(Json.encodePrettily(Arrays.asList(product1, product2)));
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}

