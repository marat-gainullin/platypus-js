/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

/**
 *
 * @author mg
 */
public class BinaryUtils {

    /**
     * Reads data from an abstract stream up to the length or up to the end of stream.
     * @param is Input strean to read
     * @param length Length of segment to be read. If length == -1, than reading is performed until the end of the stream.
     * @return Byte array containing data read from stream
     * @throws IOException
     */
    public static byte[] readStream(InputStream is, int length) throws IOException {
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        int read = 0;
        while ((read = is.read(buffer)) != -1) {
            if (length < 0 || res.size() + read <= length) {
                res.write(buffer, 0, read);
            } else {
                res.write(buffer, 0, read - (res.size() + read - length));
                break;
            }
        }
        res.flush();
        byte[] bytes = res.toByteArray();
        assert length < 0 || bytes.length == length;
        return bytes;
    }

    /**
     * Reads string data from an abstract reader up to the length or up to the end of reader.
     * @param aReader Reader to read from.
     * @param length Length of segment to be read. It length == -1, than reading is performed until the end of Reader.
     * @return String, containing data read from Reader
     * @throws IOException
     */
    public static String _readReader(Reader aReader, int length) throws IOException {
        char[] buffer = new char[1024];
        StringWriter res = new StringWriter();
        int read = 0;
        int written = 0;
        while ((read = aReader.read(buffer)) != -1) {
            if (length < 0 || written + read <= length) {
                res.write(buffer, 0, read);
                written += read;
            } else {
                res.write(buffer, 0, read - (written + read - length));
                written += length - (written + read);
                break;
            }
        }
        res.flush();
        String str = res.toString();
        assert length < 0 || str.length() == length;
        return str;
    }
}
