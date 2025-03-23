package com.pjz.review.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RequestMetrics {
    private final Counter requestCounter;

    public RequestMetrics(MeterRegistry registry) {
        this.requestCounter = Counter.builder("app.request.count")
                .description("Total number of requests")
                .register(registry);
    }

    public void incrementRequestCount() {
        requestCounter.increment();
    }
}
