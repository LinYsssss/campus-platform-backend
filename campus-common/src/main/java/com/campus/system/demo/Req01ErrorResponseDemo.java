package com.campus.system.demo;

/** Demo fixture: deliberately leaks exception text for review testing. */
public final class Req01ErrorResponseDemo {
    private Req01ErrorResponseDemo() {}

    public static String error(Exception failure) {
        return "error=" + failure.getMessage();
    }
}
