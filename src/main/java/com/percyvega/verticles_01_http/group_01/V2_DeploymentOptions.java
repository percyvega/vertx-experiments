package com.percyvega.verticles_01_http.group_01;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class V2_DeploymentOptions extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2_DeploymentOptions.class);

    public static void main(String[] args) {
        LOGGER.info("*********************************************************** Hello from " + V2_DeploymentOptions.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V2_DeploymentOptions(), getDeploymentOptions());
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("*********************************************************** Verticle App Started ***********************************************************");

        vertx
                .createHttpServer()
                .requestHandler(routingContext ->
                    routingContext.response().end(String.format("<h1>Welcome to Vert.x Intro - %s</h1>", this.getClass().getSimpleName()))
                )
                .listen(config().getInteger("http.port", 8080)); // this returns 80
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }

    private static DeploymentOptions getDeploymentOptions() {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(new JsonObject().put("http.port", 80));
        return deploymentOptions;
    }
}
