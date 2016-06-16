package ru.qsolution.vodovoz.driver.AsyncTasks;

/**
 * Created by Andrei Vinogradov on 16.06.16.
 * (c) Quality Solution Ltd.
 */

public interface IAsyncTaskListener<T> {
    void AsyncTaskCompleted(T result);
}
