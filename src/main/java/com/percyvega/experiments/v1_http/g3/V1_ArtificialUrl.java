package com.percyvega.experiments.v1_http.g3;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Arrays;

public class V1_ArtificialUrl extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(V1_ArtificialUrl.class.getName());

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + V1_ArtificialUrl.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_ArtificialUrl());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        Router router = Router.router(vertx);

        router
                .get("/api/v1/products.html")
                .handler(this::getAllProducts);

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
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }
}

