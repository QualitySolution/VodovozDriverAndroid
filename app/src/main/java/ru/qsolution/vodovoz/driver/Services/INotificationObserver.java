package ru.qsolution.vodovoz.driver.Services;

/**
 * Created by Andrei Vinogradov on 30.06.16.
 * (c) Quality Solution Ltd.
 */
public interface INotificationObserver {
    void HandleNotification();
    Boolean IsActive();
    String NotificationType();
}
