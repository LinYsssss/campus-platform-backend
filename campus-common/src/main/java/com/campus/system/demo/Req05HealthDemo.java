package com.campus.system.demo;

/** Demo fixture: stable health payload suitable for deployment checks. */
public final class Req05HealthDemo {
    private Req05HealthDemo() {}

    public static String payload(boolean databaseUp) {
        return databaseUp ? "{\"status\":\"UP\"}" : "{\"status\":\"DOWN\"}";
    }
}
