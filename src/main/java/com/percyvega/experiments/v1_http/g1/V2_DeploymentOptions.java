package com.percyvega.experiments.v1_http.g1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class V2_DeploymentOptions extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(V2_DeploymentOptions.class.getName());

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + V2_DeploymentOptions.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V2_DeploymentOptions(), getDeploymentOptions());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        vertx
                .createHttpServer()
                .requestHandler(routingContext ->
                    routingContext.response().end(String.format("<h1>Welcome to Vert.x Intro - %s</h1>", this.getClass().getSimpleName()))
                )
                .listen(config().getInteger("http.port", 8080)); // this returns 80
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }

    private static DeploymentOptions getDeploymentOptions() {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(new JsonObject().put("http.port", 80));
        return deploymentOptions;
    }
}
