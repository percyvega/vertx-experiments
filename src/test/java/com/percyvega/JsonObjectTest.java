package com.percyvega;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonObjectTest {
    private static final Logger log = LogManager.getLogger(JsonObjectTest.class.getName());

    private static final String JSON_STRING =
            "{" +
            "    \"name\" : \"Percy\"," +
            "    \"location\" : \"Miami, FL\"," +
            "    \"age\" : 39" +
            "}";

    @Test
    public void test_jsonObject_from_jsonString() {
        JsonObject jsonObject = new JsonObject(JSON_STRING);
        log.info(jsonObject.encodePrettily());
    }

    @Test
    public void test_fluency_of_jsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject
                .put("name", "Percy")
                .put("location", "Miami, FL")
                .put("age", 39);
        log.info(jsonObject.encodePrettily());

        JsonObject jsonObjectUsingString = new JsonObject(JSON_STRING);
        log.info(jsonObjectUsingString.encodePrettily());

        assertThat(jsonObject).isEqualTo(jsonObjectUsingString);
    }

    @Test
    public void test_jsonObject_mapTo_mapFrom_object() {
        Person person = new Person()
                .setName("Percy")
                .setLocation("Miami, FL")
                .setAge(39);

        JsonObject jsonObject = JsonObject.mapFrom(person);
        log.info(jsonObject.encodePrettily());

        Person person1 = jsonObject.mapTo(Person.class);
        log.info(person1.toString());
    }

}

