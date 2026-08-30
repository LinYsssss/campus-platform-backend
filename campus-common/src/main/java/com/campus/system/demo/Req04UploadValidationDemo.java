package com.campus.system.demo;

/** Demo fixture: extension-only validation accepts a forged MIME type. */
public final class Req04UploadValidationDemo {
    private Req04UploadValidationDemo() {}

    public static boolean allowed(String filename, String mime) {
        return filename.endsWith(".pdf");
    }
}
