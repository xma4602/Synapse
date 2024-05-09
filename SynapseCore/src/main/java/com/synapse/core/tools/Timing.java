package com.synapse.core.tools;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

public class Timing {
    private Instant start;
    @Getter
    private Duration duration;

    private Timing() {
        start = Instant.now();
    }

    public Duration stopTiming() {
        duration = Duration.between(start, Instant.now());
        return duration;
    }

    public Duration getMoment() {
        return Duration.between(start, Instant.now());
    }

    public static Timing startTiming() {
        return new Timing();
    }
}
