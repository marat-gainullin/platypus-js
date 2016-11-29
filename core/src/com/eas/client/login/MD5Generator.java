/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.login;

import com.eas.client.settings.SettingsConstants;
import java.security.MessageDigest;

/**
 *
 * @author mg
 */
public class MD5Generator {

    public static String generate(String aSource) throws Exception {       
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(aSource.getBytes(SettingsConstants.COMMON_ENCODING));
        byte[] digestedBytes = md.digest();
        //convert digested to hex string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digestedBytes.length; i++) {
            sb.append(Integer.toString((digestedBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();        
    }

}
