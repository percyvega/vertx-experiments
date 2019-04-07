package com.percyvega.v3_inter_verticle_communication.g1_SeparateJvms_Http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class FavoriteShapeApp extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(FavoriteShapeApp.class);

    private static final String[] COLORS = new String[] {"Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Brown", "Magenta"};

    public static final int PORT = 8080;
    public static final String HOST = "localhost";
    public static final String URL = "/isFavoriteShape";

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + FavoriteShapeApp.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FavoriteShapeApp());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        Router router = Router.router(vertx);

        router
                .get(URL)
                .handler(this::handleRequest);

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(PORT);

        Random random = new Random(System.currentTimeMillis());
        vertx.setPeriodic(5000, handler -> askAboutFavoriteColor(COLORS[random.nextInt(COLORS.length)]));
    }

    private void handleRequest(RoutingContext routingContext) {
        List<String> queryParamColor = routingContext.queryParam("shape");
        String response = Json.encodePrettily("No, " + queryParamColor.get(0) + " is not my favorite shape - " + new Date(System.currentTimeMillis()));

        routingContext
                .response()
                .setStatusCode(200)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(response);
    }

    private void askAboutFavoriteColor(String color) {
        log.info("I will ask if his favorite color is " + color + " - " + new Date(System.currentTimeMillis()));

        WebClient webClient = WebClient.create(vertx);

        webClient
                .get(FavoriteColorApp.PORT, FavoriteColorApp.HOST, FavoriteColorApp.URL)
                .addQueryParam("color", color)
                .send(httpResponse -> {
                    if (httpResponse.succeeded()) {
                        log.info(httpResponse.result().body());
                    } else {
                        log.info("httpResponse failed");
                    }
                });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
