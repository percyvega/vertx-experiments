package com.percyvega.v0_Threads;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class VertxThreads {

    private static final Logger log = LogManager.getLogger(VertxThreads.class.getName());

    private static final List<String> threadNamesAlreadyDisplayed = new ArrayList<>();
    private static int threadsCount = 0;

    private static final Map<String, AtomicInteger> verticlesPerThread = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        logThreadsInfo();
        Vertx vertx = Vertx.vertx();
        logThreadsInfo();
        runManyVerticles(vertx, false);
        logThreadsInfo();
        runManyVerticles(vertx, true);
        logThreadsInfo();

        log.info("================================================================");
        for (Map.Entry<String, AtomicInteger> entry : verticlesPerThread.entrySet()) {
            log.info(entry.getKey() + ", " + entry.getValue());
        }
    }

    private static void runManyVerticles(Vertx vertx, boolean useWorkerVerticles) throws InterruptedException {
        int verticlesCount = 1000;
        final CountDownLatch latch = new CountDownLatch(verticlesCount);
        final DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(useWorkerVerticles);
        for (int i = 0; i < verticlesCount; i++) {
            vertx.deployVerticle(new MyVerticle(verticlesPerThread), deploymentOptions, c -> latch.countDown());
        }

        latch.await();
    }

    private static void logThreadsInfo() {
        log.info("----------------------------------------------------------------");
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
        log.info("New threads count: " + (threadsCount - oldThreadsCount));
    }
}

class MyVerticle extends AbstractVerticle {
    private final Map<String, AtomicInteger> threadCounts;

    MyVerticle(Map<String, AtomicInteger> threadCounts) {
        this.threadCounts = threadCounts;
    }

    @Override
    public void start() {
        threadCounts.computeIfAbsent(Thread.currentThread().getName(),
                t -> new AtomicInteger(0)).incrementAndGet();
    }
}
