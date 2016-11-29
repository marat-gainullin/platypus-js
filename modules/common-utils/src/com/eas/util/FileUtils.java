/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common operations with files.
 *
 * @author vv
 */
public class FileUtils {

    public static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    public static String getFileExtension(String fileName) {
        String ext = null;
        int k = fileName.lastIndexOf(EXTENSION_SEPARATOR); // NOI18N
        if (k != -1) {
            ext = fileName.substring(k + 1, fileName.length());
        }
        return ext;
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static File findBrother(File aFile, String aExtension) {
        if (aFile != null) {
            String woExt = removeExtension(aFile.getAbsolutePath());
            File brother = new File(woExt + "." + aExtension);
            return brother.exists() ? brother : null;
        } else {
            return null;
        }
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    public static byte[] readBytes(File file) throws IOException {
        long len = file.length();
        if (len > Integer.MAX_VALUE) {
            throw new IOException("Too big file " + file.getPath()); // NOI18N
        }
        try (InputStream is = new FileInputStream(file)) {
            byte[] arr = new byte[(int) len];
            int pos = 0;
            while (pos < arr.length) {
                int read = is.read(arr, pos, arr.length - pos);
                if (read == -1) {
                    break;
                }
                pos += read;
            }
            if (pos != arr.length) {
                throw new IOException("Just " + pos + " bytes read from " + file.getPath()); // NOI18N
            }
            return arr;
        }
    }

    public static String readString(File file, String encoding) throws IOException {
        return new String(readBytes(file), encoding);
    }

    public static void writeBytes(File file, byte[] arr) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(arr);
        }
    }

    public static void writeString(File file, String str, String encoding) throws IOException {
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file), encoding)) {
            out.write(str);
        }
    }

    public static void delete(File f) throws IOException {
        delete(f, false);
    }

    public static void delete(File f, boolean aSkipUndeletedFiles) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c, aSkipUndeletedFiles);
            }
        }
        if (!f.delete()) {
            if (aSkipUndeletedFiles) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.WARNING, "Unable to delete file: {0} skipping.", f.getAbsolutePath());
            } else {
                throw new IOException("Failed to delete file: " + f); // NOI18N
            }
        }
    }

    public static void clearDirectory(File f, boolean aSkipUndeletedFiles) throws IOException {
        if (!f.isDirectory()) {
            throw new IllegalArgumentException("Only directory can be cleared."); // NOI18N
        }
        for (File c : f.listFiles()) {
            delete(c, aSkipUndeletedFiles);
        }
    }
}
