package com.percyvega;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonObjectTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonObjectTest.class);

    private static final String JSON_STRING =
            "{" +
            "    \"name\" : \"Percy\"," +
            "    \"location\" : \"Miami, FL\"," +
            "    \"age\" : 39" +
            "}";

    @Test
    public void test_jsonObject_from_jsonString() {
        JsonObject jsonObject = new JsonObject(JSON_STRING);
        LOGGER.info(jsonObject);
    }

    @Test
    public void test_fluency_of_jsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject
                .put("name", "Percy")
                .put("location", "Miami, FL")
                .put("age", 39);
        LOGGER.info(jsonObject);

        JsonObject jsonObjectUsingString = new JsonObject(JSON_STRING);
        LOGGER.info(jsonObjectUsingString);

        assertThat(jsonObject).isEqualTo(jsonObjectUsingString);
    }

    @Test
    public void test_jsonObject_mapTo_mapFrom_object() {
        Person person = new Person()
                .setName("Percy")
                .setLocation("Miami, FL")
                .setAge(39);

        JsonObject jsonObject = JsonObject.mapFrom(person);
        LOGGER.info(jsonObject);

        Person person1 = jsonObject.mapTo(Person.class);
        LOGGER.info(person1);
    }

}

