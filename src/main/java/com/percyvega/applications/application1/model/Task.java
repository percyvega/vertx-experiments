package com.percyvega.applications.application1.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class Task {
    private int projectId;
    private String name;
    private Long timeToComplete;
    private String dateTimeCompleted;

    public Task() {
    }

    public Task(int projectId, String name) {
        this.projectId = projectId;
        this.name = name;
    }

    public static JsonArray toJsonArray(List<Task> tasks) {
        List<JsonObject> jsonObjectList = tasks.stream().map(Task::toJsonObject).collect(Collectors.toList());
        return new JsonArray(jsonObjectList);
    }

    public static Task fromJsonObject(JsonObject jsonObject) {
        return jsonObject.mapTo(Task.class);
    }

    public JsonObject toJsonObject() {
        return JsonObject.mapFrom(this);
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeToComplete() {
        return timeToComplete;
    }

    public void setTimeToComplete(Long timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    public String getDateTimeCompleted() {
        return dateTimeCompleted;
    }

    public void setDateTimeCompleted(String dateTimeCompleted) {
        this.dateTimeCompleted = dateTimeCompleted;
    }

    @Override
    public String toString() {
        return "Task{" +
                "projectId=" + projectId +
                ", name='" + name + '\'' +
                ", timeToComplete=" + timeToComplete +
                ", dateTimeCompleted='" + dateTimeCompleted + '\'' +
                '}';
    }
}
