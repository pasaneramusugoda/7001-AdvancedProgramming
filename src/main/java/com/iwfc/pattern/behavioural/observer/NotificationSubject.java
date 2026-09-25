package com.iwfc.pattern.behavioural.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Behavioural Design Pattern: Observer Pattern (Subject / Observable).
 * Maintains a subscriber list of observers and broadcasts system events to them.
 */
public class NotificationSubject {

    private final List<NotificationObserver> observers = Collections.synchronizedList(new ArrayList<>());

    /**
     * Attaches an observer to the notification stream.
     *
     * @param observer observer to register
     */
    public void attach(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Detaches an observer.
     *
     * @param observer observer to remove
     */
    public void detach(NotificationObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    /**
     * Broadcasts an event to all attached observers.
     *
     * @param event the event to broadcast
     */
    public void notifyObservers(NotificationEvent event) {
        synchronized (observers) {
            for (NotificationObserver observer : observers) {
                try {
                    observer.onNotification(event);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }

    public int getObserverCount() {
        return observers.size();
    }
}
