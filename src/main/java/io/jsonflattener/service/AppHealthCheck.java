package io.jsonflattener.service;

import com.codahale.metrics.health.HealthCheck;

import javax.ws.rs.GET;

public class AppHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
