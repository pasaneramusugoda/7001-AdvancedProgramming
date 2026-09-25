package com.iwfc.pattern.behavioural.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete Observer that captures notifications, formats them for display,
 * and maintains an event history log.
 */
public class ConsoleNotificationObserver implements NotificationObserver {

    private final String observerName;
    private final List<NotificationEvent> eventLog = Collections.synchronizedList(new ArrayList<>());
    private boolean printToConsole = true;

    public ConsoleNotificationObserver(String observerName) {
        this.observerName = observerName;
    }

    public ConsoleNotificationObserver(String observerName, boolean printToConsole) {
        this.observerName = observerName;
        this.printToConsole = printToConsole;
    }

    @Override
    public void onNotification(NotificationEvent event) {
        eventLog.add(event);
        if (printToConsole) {
            System.out.printf("[NOTIFICATION %s] %s -> %s%n",
                    observerName, event.getTitle(), event.getMessage());
        }
    }

    public List<NotificationEvent> getEventLog() {
        synchronized (eventLog) {
            return new ArrayList<>(eventLog);
        }
    }

    public void clearLog() {
        eventLog.clear();
    }
}
