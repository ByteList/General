package de.gamechest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by nemmerich on 11.09.2018.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class AsyncTasks {

    private static AsyncTasks instance;

    private Executor executor;

    private AsyncTasks() {
        this.executor = Executors.newCachedThreadPool();
    }

    public void runTaskAsync(Runnable runnable) {
        this.executor.execute(runnable);
    }

    public void runTaskAsyncLater(Runnable runnable, long sleepSeconds) {
        long check = System.currentTimeMillis() / 1000 + sleepSeconds;

        runTaskAsync(()-> {
            while (Thread.interrupted()) {
                if(System.currentTimeMillis() / 1000 >= check) break;
            }
            runnable.run();
        });
    }


    public static AsyncTasks getInstance() {
        if(instance == null) {
            instance = new AsyncTasks();
        }
        return instance;
    }
}
