package org.benetech.servicenet.adapter.eden.model;

public enum Weekday {
    MON(0),
    TUE(1),
    WED(2),
    THU(3),
    FRI(4),
    SAT(5),
    SUN(6);

    private int number;

    Weekday(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
