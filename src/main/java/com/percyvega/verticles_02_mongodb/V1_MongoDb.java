package com.percyvega.verticles_02_mongodb;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.List;

import static com.mongodb.client.model.Filters.regex;

public class V1_MongoDb extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(V1_MongoDb.class);

    private static MongoClient mongoClient;

    public static void main(String[] args) {
        log.info("*********************************************************** Hello from " + V1_MongoDb.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_MongoDb());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        initMongoClient();

        Router router = Router.router(vertx);
        router
                .get("/mongofind")
                .handler(this::getAllProducts);
        router
                .route()
                .handler(StaticHandler.create().setCachingEnabled(false));

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    private void initMongoClient() {
        JsonObject dbConfig = new JsonObject();
        dbConfig.put("connection_string", "mongodb://localhost:27017/MongoTest");
        dbConfig.put("useObjectId", true);
        mongoClient = MongoClient.createShared(vertx, dbConfig);
    }

    private void getAllProducts(RoutingContext routingContext) {
        FindOptions findOptions = new FindOptions().setLimit(5);

        JsonObject fieldNameContainsR = new JsonObject().put("name", new JsonObject().put("$regex", ".*r.*"));

        mongoClient.findWithOptions("users", fieldNameContainsR, findOptions, results -> {
            try {
                List<JsonObject> objectList = results.result();
                if (objectList == null || objectList.size() == 0) {
                    log.info("No results found");
                }

                routingContext
                        .response()
                        .setStatusCode(200)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(Json.encodePrettily(objectList));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}

