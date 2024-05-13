package com.synapse.core.tools;

import java.util.List;

public interface Reportable {
    List<String> getReport();

    default void printReport(){
        getReport().forEach(System.out::print);
    }

    default void addSubReports(List<String> report, List<String> subreports){
        subreports.forEach(r -> report.add("\t" + r));
    }
}
