package com.percyvega.verticles_01_http.group_01;

import io.vertx.core.Vertx;

public class V0_Vertx_CreateHttpServer {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.createHttpServer()
                .requestHandler(req -> req.response()
                        .end("*********************************************************** Hello, request handled from " + Thread.currentThread().getName()))
                .listen(8080);
    }
}
