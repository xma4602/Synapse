package com.synapse.core.tools;

import java.util.List;

public interface Reportable {
    List<String> getReport();

    default void printReport(){
        getReport().forEach(System.out::print);
    }
}
