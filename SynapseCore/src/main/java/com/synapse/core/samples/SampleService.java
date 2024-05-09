package com.synapse.core.samples;

import com.synapse.core.tools.Reportable;

public interface SampleService extends Reportable {

    Iterable<Sample> getTrainingSampling();

    Iterable<Sample> getTestingSampling();
}

