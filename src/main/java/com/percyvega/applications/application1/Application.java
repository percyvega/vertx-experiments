package com.percyvega.applications.application1;

import com.percyvega.applications.application1.model.Task;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.percyvega.applications.application1.EventLoopVerticle.EVENT_LOOP_VERTICLE_EVENT_BUS;

public class Application {
    private static final Logger log = LogManager.getLogger(Application.class);

    private static final int PROJECTS_COUNT = 3;
    private static final int TASKS_PER_PROJECT_COUNT = 5;

    private static final int EVENT_LOOP_VERTICLE_COUNT = 2;
    private static final int WORKER_POOL_SIZE = 3;

    public static void main(String[] args) {

        final Vertx vertx = setup();

        List<Future> futureList = new ArrayList<>();
        for (int iProject = 0; iProject < PROJECTS_COUNT; iProject++) {
            Future<Message<Object>> messageFuture = Future.future();
            futureList.add(messageFuture);
            log.info("-------------- About to send project {} to {} -------------", iProject, EVENT_LOOP_VERTICLE_EVENT_BUS);
            vertx.eventBus().send(EVENT_LOOP_VERTICLE_EVENT_BUS, getProjectTasksPayload(iProject), messageFuture);
        }

        log.info("Finished sending all projects {}", EVENT_LOOP_VERTICLE_EVENT_BUS);

        long start = System.currentTimeMillis();
        CompositeFuture.all(futureList).setHandler(event -> {
            long finished = System.currentTimeMillis();
            if (event.succeeded()) {
                List<String> projectIds = new ArrayList<>(PROJECTS_COUNT);
                event
                        .result()
                        .list()
                        .forEach(o -> projectIds.add(((Message) o).body().toString()));

                log.info("All project futures succeeded after {} ms. for projects {}", finished - start, projectIds);
            } else {
                log.error("All project futures completed after {} ms. but at least one failed", finished - start);
            }
            vertx.close();
        });
    }

    private static Vertx setup() {
        logThreadsInfo("Just started main()");

        final Vertx vertx = Vertx.vertx();

        logThreadsInfo("Just instantiated Vertx.vertx()");

        DeploymentOptions eventLoopVerticleDeploymentOptions = new DeploymentOptions()
                .setInstances(EVENT_LOOP_VERTICLE_COUNT);
        DeploymentOptions workerDeploymentOptions = new DeploymentOptions()
                .setWorker(true)
                .setInstances(WORKER_POOL_SIZE);

        vertx.deployVerticle(EventLoopVerticle.class.getName(), eventLoopVerticleDeploymentOptions);

        logThreadsInfo("Just deployed an event loop verticle");

        vertx.deployVerticle(WorkerVerticle.class.getName(), workerDeploymentOptions);

        logThreadsInfo("Just deployed a worker verticle with " + WORKER_POOL_SIZE + " instances");
        return vertx;
    }

    private static JsonObject getProjectTasksPayload(int projectId) {
        List<Task> tasks = new ArrayList<>(TASKS_PER_PROJECT_COUNT);
        for (int i = 0; i < TASKS_PER_PROJECT_COUNT; i++) {
            tasks.add(new Task(projectId, "Task" + i));
        }

        JsonArray tasksJsonArray = Task.toJsonArray(tasks);

        return new JsonObject()
                .put("projectId", projectId)
                .put("tasksJsonArray", tasksJsonArray);
    }

    private static int threadsCount = 0;
    private static final List<String> threadNamesAlreadyDisplayed = new ArrayList<>();

    private static void logThreadsInfo(String actionCompleted) {
        log.info("<<<<<<<<<<<<<<< " + actionCompleted + ". Threads: <<<<<<<<<<<<<<<");
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        int oldThreadsCount = threadsCount;
        for (Thread thread : allStackTraces.keySet()) {
            String threadName = thread.getName();
            if (!threadNamesAlreadyDisplayed.contains(threadName)) {
                log.info(threadName);
                ++threadsCount;
                threadNamesAlreadyDisplayed.add(threadName);
            }
        }
        log.info(">>>>>>>>>>> New thread count: " + (threadsCount - oldThreadsCount) + " >>>>>>>>>>>>>");
    }

}
