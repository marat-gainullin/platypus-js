/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import java.text.ParseException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class CookieTest {

    private static final String DOMAIN_PATH_SAMPLE1 = " LSID=DQAAAKEaem_vYg; Domain=docs.foo.com; Path=/accounts; Expires=Fri, 13-Jan-2012 22:23:01 GMT; Secure; HttpOnly";
    private static final String DOMAIN_PATH_SAMPLE2 = "HSID=AYQEVnDKrdst; Domain=.foo.com; Path=/; Expires=Fri, 13-Jan-2012 22:23:01 GMT; HttpOnly";
    private static final String DOMAIN_PATH_SAMPLE3 = " SSID=Ap4PGTEq; Domain=.foo.com; Path=/; Expires=Fri, 13-Jan-2012 22:23:01 GMT; Secure;";
    private static final String EXPIRES_MAX_AGE_SAMPLE1 = "lu= Rg3vHJZnehYLjVg7qi3bZjzg; Expires=Sun, 15 Jan 2012 21:47:38 GMT; Path=/;  HttpOnly";
    private static final String EXPIRES_MAX_AGE_SAMPLE2 = " made_write_conn = 1295214458 ;   Domain=foo.com  ; Max-Age = 456123";
    private static final String EXPIRES_MAX_AGE_SAMPLE3 = "reg_fb_gate=deleted; Expires=Mon, 01 Jan 1990 00:00:01 GMT; Path=/; Domain=.foo.com;";

    @Test
    public void domainPath1Test() throws ParseException {
        Cookie cookie = Cookie.parse(DOMAIN_PATH_SAMPLE1);
        assertNotNull(cookie.getName());
        assertEquals("LSID", cookie.getName());
        assertEquals("DQAAAKEaem_vYg", cookie.getValue());
        assertTrue(cookie.isSecure());
        assertTrue(cookie.isHttpOnly());
        assertEquals("docs.foo.com", cookie.getDomain());
        assertEquals("/accounts", cookie.getPath());
    }

    @Test
    public void domainPath2Test() throws ParseException {
        Cookie cookie = Cookie.parse(DOMAIN_PATH_SAMPLE2);
        assertNotNull(cookie.getName());
        assertEquals("HSID", cookie.getName());
        assertEquals("AYQEVnDKrdst", cookie.getValue());
        assertFalse(cookie.isSecure());
        assertTrue(cookie.isHttpOnly());
        assertEquals(".foo.com", cookie.getDomain());
        assertEquals("/", cookie.getPath());
    }

    @Test
    public void domainPath3Test() throws ParseException {
        Cookie cookie = Cookie.parse(DOMAIN_PATH_SAMPLE3);
        assertNotNull(cookie.getName());
        assertEquals("SSID", cookie.getName());
        assertEquals("Ap4PGTEq", cookie.getValue());
        assertTrue(cookie.isSecure());
        assertFalse(cookie.isHttpOnly());
        assertEquals(".foo.com", cookie.getDomain());
        assertEquals("/", cookie.getPath());
    }

    @Test
    public void expiresMaxAge1Test() throws ParseException {
        Cookie cookie = Cookie.parse(EXPIRES_MAX_AGE_SAMPLE1);
        assertNotNull(cookie.getName());
        assertEquals("lu", cookie.getName());
        assertEquals("Rg3vHJZnehYLjVg7qi3bZjzg", cookie.getValue());
        assertFalse(cookie.isSecure());
        assertTrue(cookie.isHttpOnly());
        assertNull(cookie.getDomain());
        assertEquals("/", cookie.getPath());
    }

    @Test
    public void expiresMaxAge2Test() throws ParseException {
        Cookie cookie = Cookie.parse(EXPIRES_MAX_AGE_SAMPLE2);
        assertNotNull(cookie.getName());
        assertEquals("made_write_conn", cookie.getName());
        assertEquals("1295214458", cookie.getValue());
        assertFalse(cookie.isSecure());
        assertFalse(cookie.isHttpOnly());
        assertEquals("foo.com", cookie.getDomain());
        assertNull(cookie.getPath());
        assertEquals(456123000, cookie.getMaxAge());
    }

    @Test
    public void expiresMaxAge3Test() throws ParseException {
        Cookie cookie = Cookie.parse(EXPIRES_MAX_AGE_SAMPLE3);
        assertNotNull(cookie.getName());
        assertEquals("reg_fb_gate", cookie.getName());
        assertEquals("deleted", cookie.getValue());
        assertFalse(cookie.isSecure());
        assertFalse(cookie.isHttpOnly());
        assertEquals(".foo.com", cookie.getDomain());
        assertEquals("/", cookie.getPath());
    }
}
