package com.percyvega.experiments.v0_Threads;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VertxThreads {

    private static final Logger log = LogManager.getLogger(VertxThreads.class.getName());

    private static final String WORKER = "worker";
    private static final String STANDARD = "standard (event loop)";

    private static final int verticlesCount = 50; // I requested 50 event loop and 50 worker threads, but only 8 event loop and 20 worker threads will be created

    private static final Map<String, AtomicInteger> verticlesPerThread = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        logThreadsInfo("Started application");

        Vertx vertx = Vertx.vertx();
        logThreadsInfo("Instantiated Vertx");

        boolean useWorkerVerticles = false;
        runManyVerticles(vertx, verticlesCount, useWorkerVerticles);
        logThreadsInfo(verticlesCount, useWorkerVerticles);

        useWorkerVerticles = true;
        runManyVerticles(vertx, verticlesCount, useWorkerVerticles);
        logThreadsInfo(verticlesCount, useWorkerVerticles);

        log.info("======================= " + verticlesCount + " increments performed by " + STANDARD + " threads and " + verticlesCount + " increments performed by " + WORKER + " threads =========================");

        Map<String, AtomicInteger> sortedVerticlesPerThread = verticlesPerThread.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        for (Map.Entry<String, AtomicInteger> entry : sortedVerticlesPerThread.entrySet()) {
            log.info(entry.getKey() + ", " + entry.getValue());
        }
    }

    private static void runManyVerticles(Vertx vertx, int verticlesCount, boolean useWorkerVerticles) throws InterruptedException {
        final DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(useWorkerVerticles);

        final CountDownLatch latch = new CountDownLatch(verticlesCount);
        for (int i = 0; i < verticlesCount; i++) {
            vertx.deployVerticle(new MyVerticle(verticlesPerThread), deploymentOptions, c -> latch.countDown());
        }
        latch.await();
    }

    private static void logThreadsInfo(int verticlesCount, boolean useWorkerVerticles) {
        logThreadsInfo("Finished running " + verticlesCount + " " + (useWorkerVerticles ? WORKER : STANDARD) + " " + "verticles");
    }

    private static int threadsCount = 0;
    private static final List<String> threadNamesAlreadyDisplayed = new ArrayList<>();
    private static void logThreadsInfo(String actionCompleted) {
        log.info("<<<<<<<<<<<<<<< " + actionCompleted + ". Threads: <<<<<<<<<<<<<<<");
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        int oldThreadsCount = threadsCount;
        for (Thread thread : allStackTraces.keySet()) {
            String threadName = thread.getName();
            if(!threadNamesAlreadyDisplayed.contains(threadName)) {
                log.info(threadName);
                ++threadsCount;
                threadNamesAlreadyDisplayed.add(threadName);
            }
        }
        log.info(">>>>>>>>>>> New thread count: " + (threadsCount - oldThreadsCount) + " >>>>>>>>>>>>>");
    }
}

