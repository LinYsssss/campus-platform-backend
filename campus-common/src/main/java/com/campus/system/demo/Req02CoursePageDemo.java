package com.campus.system.demo;

import java.util.List;

/** Demo fixture: missing pagination makes the query unbounded. */
public final class Req02CoursePageDemo {
    private Req02CoursePageDemo() {}

    public static List<String> listAll(List<String> courses) {
        return courses;
    }
}
