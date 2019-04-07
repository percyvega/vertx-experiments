package com.percyvega.v1_http.group_04;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductResources {

    private static final Logger log = LoggerFactory.getLogger(ProductResources.class);

    private static List<Product> products = new ArrayList<>(Arrays.asList(
            new Product(1, "My item 1"),
            new Product(2, "My item 2"),
            new Product(3, "My item 3"),
            new Product(4, "My item 4"),
            new Product(5, "My item 5")));

    public static Router getSubRouterRouter(Vertx vertx) {
        Router apiSubRouter = Router.router(vertx);

        apiSubRouter.route("/*").handler(ProductResources::defaultProcessorForAllApi);

        apiSubRouter.route("/v1/products*").handler(BodyHandler.create());
        apiSubRouter.get("/v1/products").handler(ProductResources::getAllProducts);
        apiSubRouter.get("/v1/products/:id").handler(ProductResources::getProduct);
        apiSubRouter.post("/v1/products").handler(ProductResources::createProduct);
        apiSubRouter.put("/v1/products/:id").handler(ProductResources::updateProduct);
        apiSubRouter.delete("/v1/products/:id").handler(ProductResources::deleteProduct);
        return apiSubRouter;
    }

    public static void defaultProcessorForAllApi(RoutingContext routingContext) {
        String authToken = routingContext.request().headers().get("AuthToken");

        if(authToken == null || !authToken.equals("123")) {
            log.info("Failed basic auth check");
            routingContext
                    .response()
                    .setStatusCode(401)
                    .end(Json.encodePrettily(new JsonObject().put("error", "Not Authorized to use these APIs")));
        } else {
            log.info("Passed basic auth check");
            routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE");

            routingContext.next();
        }
    }

    public static void getAllProducts(RoutingContext routingContext) {
        routingContext
                .response()
                .setStatusCode(200)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(Json.encodePrettily(products));
    }

    public static void getProduct(RoutingContext routingContext) {
        Optional<Product> product = getProductOptional(routingContext);

        if (product.isPresent()) {
            routingContext
                    .response()
                    .setStatusCode(200)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(Json.encodePrettily(product.get()));
        } else {
            routingContext
                    .response()
                    .setStatusCode(404)
                    .end();
        }
    }

    public static void createProduct(RoutingContext routingContext) {
        long maxId = products.stream().mapToLong(Product::getId).max().orElse(0);

        Product productToCreate = Json.decodeValue(routingContext.getBodyAsString(), Product.class);
        products.add(productToCreate.setId(++maxId));

        routingContext
                .response()
                .setStatusCode(201)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(Json.encodePrettily(productToCreate));
    }

    public static void updateProduct(RoutingContext routingContext) {
        Optional<Product> productOptional = getProductOptional(routingContext);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            String description = routingContext.getBodyAsJson().getString("description");
            product.setDescription(description);

            routingContext
                    .response()
                    .setStatusCode(200)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(Json.encodePrettily(product));
        } else {
            routingContext
                    .response()
                    .setStatusCode(404)
                    .end();
        }
    }

    public static void deleteProduct(RoutingContext routingContext) {
        Optional<Product> productOptional = getProductOptional(routingContext);

        if (productOptional.isPresent()) {
            products.remove(productOptional.get());

            routingContext
                    .response()
                    .setStatusCode(200)
                    .end();
        } else {
            routingContext
                    .response()
                    .setStatusCode(404)
                    .end();
        }
    }

    private static Optional<Product> getProductOptional(RoutingContext routingContext) {
        long id = Long.parseLong(routingContext.request().getParam("id"));
        return products.stream().filter(p -> p.getId() == id).findAny();
    }

}
