package ru.qsolution.vodovoz.driver.AsyncTasks;

/**
 * Created by Andrei Vinogradov on 16.06.16.
 * (c) Quality Solution Ltd.
 */

public class AsyncTaskResult <T>{
    private T result;
    private Exception exception;

    public T getResult() {
        return result;
    }

    public Exception getException () {
        return exception;
    }

    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    public AsyncTaskResult(Exception exception) {
        super();
        this.exception = exception;
    }
}
