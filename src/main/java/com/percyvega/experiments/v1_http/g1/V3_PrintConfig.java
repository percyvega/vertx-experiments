package com.percyvega.experiments.v1_http.g1;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class V3_PrintConfig extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(V3_PrintConfig.class.getName());

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + V3_PrintConfig.class.getSimpleName());

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
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        vertx
                .createHttpServer()
                .requestHandler(routingContext ->
                    routingContext.response().end(String.format("<h1>Welcome to Vert.x Intro - %s</h1>", this.getClass().getSimpleName()))
                )
                .listen(config().getInteger("http.port", 8080));
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }
}
