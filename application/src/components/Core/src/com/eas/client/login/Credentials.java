/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

/**
 *
 * @author mg
 */
public class Credentials {

    public String userName;
    public String password;

    public Credentials(String aUserName, String aPassword) {
        userName = aUserName;
        password = aPassword;
    }
}
