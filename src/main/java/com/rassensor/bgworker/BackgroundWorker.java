package com.rassensor.bgworker;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BackgroundWorker{

    //🤡
    private final Executor queue = Executors.newSingleThreadExecutor();

    public void addTaskToQueue(Runnable task) {
        queue.execute(task);
    }


}