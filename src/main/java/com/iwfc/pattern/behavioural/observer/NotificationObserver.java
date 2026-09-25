package com.iwfc.pattern.behavioural.observer;

/**
 * Behavioural Design Pattern: Observer Pattern (Observer interface).
 * Defines the update contract for listeners receiving asynchronous IWFC system events.
 */
public interface NotificationObserver {

    /**
     * Callback invoked whenever a subject broadcasts an event.
     *
     * @param event the notification event payload
     */
    void onNotification(NotificationEvent event);
}
