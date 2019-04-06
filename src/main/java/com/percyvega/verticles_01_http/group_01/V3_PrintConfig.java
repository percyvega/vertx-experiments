package com.percyvega.verticles_01_http.group_01;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class V3_PrintConfig extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(V3_PrintConfig.class);

    public static void main(String[] args) {
        log.info("*********************************************************** Hello from " + V3_PrintConfig.class.getSimpleName());

        Vertx vertx = Vertx.vertx();

        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
        configRetriever.getConfig(completionHandler -> {
            if(completionHandler.succeeded()) {
                JsonObject config = completionHandler.result();
                log.info(config.encodePrettily());
                DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config);
                vertx.deployVerticle(new V3_PrintConfig(), deploymentOptions);
            }
        });
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        vertx
                .createHttpServer()
                .requestHandler(routingContext ->
                    routingContext.response().end(String.format("<h1>Welcome to Vert.x Intro - %s</h1>", this.getClass().getSimpleName()))
                )
                .listen(config().getInteger("http.port", 8080));
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
