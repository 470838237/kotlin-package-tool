package com.honor.common.tools;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static String readFile(String filePath) {
        StringBuilder builder = readFile(filePath, false);
        return builder == null ? null : builder.toString();
    }

    public static StringBuilder readFile(String filePath, boolean isUpdateLastTime) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (isUpdateLastTime)
                    file.setLastModified(System.currentTimeMillis());
                return readFile(new FileInputStream(file));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    public static StringBuilder readFile(InputStream stream) {
        StringBuilder fileContent = new StringBuilder("");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line + "\n");
            }
            reader.close();
            return fileContent;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean writeFile(String filePath, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            File parentFile = new File(filePath).getParentFile();
            if (!parentFile.exists())
                parentFile.mkdirs();
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean deleteFile(String path) {
        if (path == null || "".equals(path)) {
            return true;
        }

        File source = new File(path);
        final File file = new File(source.getAbsolutePath() + System.currentTimeMillis());
        source.renameTo(file);
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        deleteFile(f.getAbsolutePath());
                    }
                }
                return file.delete();
            }
            return false;
        }
        return true;
    }

}
