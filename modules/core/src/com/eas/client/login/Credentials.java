/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Credentials)) {
            return false;
        }
        Credentials other = (Credentials) obj;
        if (userName == null ? other.userName != null : !userName.equals(other.userName)) {
            return false;
        }
        if (password == null ? other.password != null : !password.equals(other.password)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.userName);
        hash = 53 * hash + Objects.hashCode(this.password);
        return hash;
    }

}
