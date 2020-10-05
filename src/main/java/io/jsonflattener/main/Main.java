package io.jsonflattener.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.jsonflattener.controller.JsonFlattenerController;
import io.jsonflattener.service.AppHealthCheck;
import io.jsonflattener.service.FlattenerService;


public class Main extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new Main().run("server", "example.yml");
    }

    @Override
    public void run(Configuration configuration, Environment e) {
        FlattenerService service = new FlattenerService(new ObjectMapper());
        e.jersey().register(new JsonFlattenerController(service));

        e.healthChecks().register("up", new AppHealthCheck());
    }
}
