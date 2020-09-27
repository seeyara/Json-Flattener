package io.jsonflattener.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.jsonflattener.controller.JsonFlattenerController;
import io.jsonflattener.service.FlattenerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new Main().run("server", "example.yml");
    }

    @Override
    public void run(Configuration configuration, Environment e){
        FlattenerService service = new FlattenerService(new ObjectMapper());
        e.jersey().register(new JsonFlattenerController(service));
    }
}
