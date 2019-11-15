package com.percyvega.applications.application1;

import com.percyvega.applications.application1.model.Task;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Random;

public class WorkerVerticle extends AbstractVerticle {
    private static final Logger log = LogManager.getLogger(WorkerVerticle.class);

    public static final String WORKER_VERTICLE_EVENT_BUS = "WORKER_VERTICLE_EVENT_BUS";

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().localConsumer(WORKER_VERTICLE_EVENT_BUS, objectMessage -> {
            JsonObject jsonObject = new JsonObject(objectMessage.body().toString());
            Task task = Task.fromJsonObject(jsonObject);

            long timeToComplete = RANDOM.nextInt(1000);
            task.setTimeToComplete(timeToComplete);
            log.info("About to perform blocking IO work on {} for {} ms.", task, task.getTimeToComplete());
            performBlockingIOWork(task);

            objectMessage.reply(task.toJsonObject());
        });
    }

    private void performBlockingIOWork(Task task) {
        try {
            Thread.sleep(task.getTimeToComplete());
            task.setDateTimeCompleted(LocalDateTime.now().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
