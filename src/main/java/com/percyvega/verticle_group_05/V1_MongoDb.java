package com.percyvega.verticle_group_05;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(V1_MongoDb.class);

    private static MongoClient mongoClient;

    public static void main(String[] args) {
        LOGGER.info("Hello from " + V1_MongoDb.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_MongoDb());
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Verticle App Started");

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
                    LOGGER.info("No results found");
                }

                routingContext
                        .response()
                        .setStatusCode(200)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(Json.encodePrettily(objectList));
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        });
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Verticle App Stopped");
    }
}

