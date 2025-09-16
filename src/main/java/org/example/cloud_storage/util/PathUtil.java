package org.example.cloud_storage.util;

public class PathUtil {

    public static String getParentPath(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return "";
        }

        String path = removeTrailingSlash(fullPath);

        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex < 0) {
            return "";
        }

        if (lastSlashIndex == 0) {
            return "/";
        }

        return path.substring(0, lastSlashIndex + 1);
    }

    public static boolean isDirectory(String path) {
        return path.endsWith("/");
    }

    public static String getFileName(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return "";
        }

        String path = removeTrailingSlash(fullPath);
        int lastSlashIndex = path.lastIndexOf('/');

        if (lastSlashIndex < 0) {
            return path;
        }

        return path.substring(lastSlashIndex + 1);
    }

    private static String removeTrailingSlash(String path) {
        if (path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}
