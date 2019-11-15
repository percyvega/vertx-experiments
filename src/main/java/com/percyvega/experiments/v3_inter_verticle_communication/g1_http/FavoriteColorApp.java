package com.percyvega.experiments.v3_inter_verticle_communication.g1_http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class FavoriteColorApp extends AbstractVerticle {
    private static final Logger log = LogManager.getLogger(FavoriteColorApp.class.getName());

    private static final String[] SHAPES = new String[] {"Square", "Rectangle", "Triangle", "Diamond", "Circle", "Oval", "Trapezoid"};

    public static final int PORT = 8090;
    public static final String HOST = "localhost";
    public static final String URL = "/isFavoriteColor";

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + FavoriteColorApp.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FavoriteColorApp());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        Router router = Router.router(vertx);

        router
                .get(URL)
                .handler(this::handleRequest);

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(PORT);

        Random random = new Random(System.currentTimeMillis());
        vertx.setPeriodic(5000, handler -> askAboutFavoriteShape(SHAPES[random.nextInt(SHAPES.length)]));
    }

    private void handleRequest(RoutingContext routingContext) {
        List<String> queryParamColor = routingContext.queryParam("color");
        String response = Json.encodePrettily("No, " + queryParamColor.get(0) + " is not my favorite color - " + new Date(System.currentTimeMillis()));

        routingContext
                .response()
                .setStatusCode(200)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(response);
    }

    private void askAboutFavoriteShape(String shape) {
        log.info("I will ask if his favorite shape is " + shape + " - " + new Date(System.currentTimeMillis()));

        WebClient webClient = WebClient.create(vertx);

        webClient
                .get(FavoriteShapeApp.PORT, FavoriteShapeApp.HOST, FavoriteShapeApp.URL)
                .addQueryParam("shape", shape)
                .send(httpResponse -> {
                    if (httpResponse.succeeded()) {
                        log.info(httpResponse.result().body().toString());
                    } else {
                        log.info("httpResponse failed");
                    }
                });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }
}
