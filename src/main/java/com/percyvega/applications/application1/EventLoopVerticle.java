package com.percyvega.applications.application1;

import com.percyvega.applications.application1.model.Task;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.percyvega.applications.application1.WorkerVerticle.WORKER_VERTICLE_EVENT_BUS;

public class EventLoopVerticle extends AbstractVerticle {
    private static final Logger log = LogManager.getLogger(EventLoopVerticle.class);

    public static final String EVENT_LOOP_VERTICLE_EVENT_BUS = "EVENT_LOOP_VERTICLE_EVENT_BUS";

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().localConsumer(EVENT_LOOP_VERTICLE_EVENT_BUS, event -> {
            JsonObject projectJsonObject = new JsonObject(event.body().toString());

            int projectId = projectJsonObject.getInteger("projectId");
            JsonArray tasksJsonArray = projectJsonObject.getJsonArray("tasksJsonArray");

            List<Future> futureList = new ArrayList<>();
            log.info("About to send each task to {} for project {}", WORKER_VERTICLE_EVENT_BUS, projectId);
            for (int iTask = 0; iTask < tasksJsonArray.size(); iTask++) {
                JsonObject taskJsonObject = tasksJsonArray.getJsonObject(iTask);

                Future<Message<Object>> messageFuture = Future.future();
                futureList.add(messageFuture);
                vertx.eventBus().send(WORKER_VERTICLE_EVENT_BUS, taskJsonObject, messageFuture);
            }

            log.info("Finished sending all tasks to {} for project {}", WORKER_VERTICLE_EVENT_BUS, projectId);
            long start = System.currentTimeMillis();

            CompositeFuture.all(futureList).setHandler(event1 -> {
                long finished = System.currentTimeMillis();
                if (event1.succeeded()) {
                    log.info("All task futures succeeded after {} ms. for project {}", finished - start, projectId);
                    event1
                            .result()
                            .list()
                            .forEach(o -> {
                                Task task = Task.fromJsonObject(JsonObject.mapFrom(((Message) o).body()));
                                log.info("Reading the result of processing {}", task);
                            });
                } else {
                    log.error("All task futures completed after {} ms. but at least one failed", finished - start);
                }
                log.info("Finished processing all task future replies for project {}", projectId);
                event.reply(projectId);
            });
        });
    }
}
