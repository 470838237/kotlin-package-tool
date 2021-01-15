package com.honor.common.tools;


import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {

    private static final Object LOCKER = new Object();

    public static String getStackTrace(Throwable e) {
        if (e == null)
            return null;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String error;
        try {
            e.printStackTrace(pw);
            error = sw.toString();
        } finally {
            pw.close();
        }
        return error;
    }

}
