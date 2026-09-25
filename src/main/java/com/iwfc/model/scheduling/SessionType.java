package com.iwfc.model.scheduling;

/**
 * Categorization of group and individual fitness sessions hosted at IWFC.
 */
public enum SessionType {
    HIIT("High Intensity Interval Training"),
    YOGA("Vinyasa & Mindful Yoga"),
    PILATES("Mat & Reformer Pilates"),
    SPIN_CLASS("Group Indoor Cycling"),
    STRENGTH_CIRCUIT("Functional Strength Circuit"),
    CARDIO_BLAST("Cardio Endurance Blast");

    private final String title;

    SessionType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
