package com.synapse.core.tools;

public interface Monitored {

    String getProcessName();

    double getProgress();

    Iterable<? extends Monitored> getProcessComponents();

}
