package com.synapse.core.experimentation;

import com.synapse.core.tools.Monitored;
import com.synapse.core.training.teachers.MiddleTeacher;
import com.synapse.core.training.teachers.Teacher;
import com.synapse.core.training.TrainingResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.Callable;

public class Experiment implements Callable<TrainingResult>, Monitored {
    @Setter
    private String experimentName;
    @Getter
    @Setter
    private ExperimentParameters experimentParameters;
    private Teacher teacher;

    public Experiment(ExperimentParameters experimentParameters) {
        this.experimentParameters = experimentParameters;
    }

    @Override
    public TrainingResult call() {
        teacher = new MiddleTeacher();
        teacher.setTeacherName(experimentName + "-" + "teacher");
        teacher.setParameters(experimentParameters);
        return teacher.call();
    }


    @Override
    public String getProcessName() {
        return experimentName;
    }

    @Override
    public double getProgress() {
        return teacher.getProgress();
    }

    @Override
    public Iterable<? extends Monitored> getProcessComponents() {
        return List.of(teacher);
    }
}
