/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Andrew
 */
public class HttpContextTests {
    
    private HttpURLConnection getConnection(String aURL) throws MalformedURLException, IOException {
        URL url = new URL(aURL);
        return (HttpURLConnection)url.openConnection();
    }
    
    private void checkResponceCode(HttpURLConnection aConnection) throws IOException {
         assertEquals(aConnection.getResponseCode(), 200);
    }
    
    @Test
    public void addHeaderTest() throws Exception {
        HttpURLConnection httpCon = getConnection("http://localhost:8080/test/application/api?__type=14&__moduleName=TestHttpContext&__methodName=changeParam");
        checkResponceCode(httpCon);
        assertEquals(httpCon.getHeaderField("test"), "test");
    }
    
    @Test
    public void addCookieTest() throws Exception {
        HttpURLConnection httpCon = getConnection("http://localhost:8080/test/application/api?__type=14&__moduleName=TestHttpContext&__methodName=addCookie");
        checkResponceCode(httpCon);
        assertTrue(httpCon.getHeaderField("Set-Cookie").indexOf("test=tost; Version=1; Comment=\"test test\"; Max-Age=100; Expires=") == 0);
    }
    
    @Test
    public void changeBodyTest() throws Exception {
        HttpURLConnection httpCon = getConnection("http://localhost:8080/test/application/api?__type=14&__moduleName=TestHttpContext&__methodName=changeBody");
        checkResponceCode(httpCon);
        assertEquals(httpCon.getContentLength(), 683);
        String type = httpCon.getContentType();
        String charset = "";
        String[] values = type.split(";");
        for (String value : values) {
            if (value.toLowerCase().startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }
        charset = charset.isEmpty() ?  "utf-8" : charset;
        String text = new String(BinaryUtils.readStream(httpCon.getInputStream(), httpCon.getContentLength()), charset);
        assertEquals(text, "Фишер: \"В 1970 году в Бледе я принял участие в международном \n" + 
            "блицтурнире. В партии с Петросяном мы то и дело обменивались шахами, причем он произносил \n" +
            "это слово по-русски, а я – по-английски. В момент, когда у обоих уже начали зависать флажки,\n" +
            "я вдруг сказал по-русски: \"Вам шах, гроссмейстер!\" Петросян настолько поразился, что на какой-то \n" +
            "момент забыл о флажке и просрочил время.");
        
    }
}
