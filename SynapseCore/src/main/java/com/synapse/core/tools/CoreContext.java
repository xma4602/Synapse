package com.synapse.core.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreContext {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newWorkStealingPool();

}
