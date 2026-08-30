package com.campus.system.demo;

/** Demo fixture: publishes without recording the operator. */
public final class Req03NoticeAuditDemo {
    private Req03NoticeAuditDemo() {}

    public static String publish(String text, long operatorId) {
        return "published:" + text;
    }
}
