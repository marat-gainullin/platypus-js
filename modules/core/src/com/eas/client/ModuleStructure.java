/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ModuleStructure {

    protected AppElementFiles parts = new AppElementFiles();
    protected Set<String> clientDependencies = new HashSet<>();
    protected Set<String> serverDependencies = new HashSet<>();
    protected Set<String> queryDependencies = new HashSet<>();

    public AppElementFiles getParts() {
        return parts;
    }

    public Set<String> getClientDependencies() {
        return clientDependencies;
    }

    public Set<String> getServerDependencies() {
        return serverDependencies;
    }

    public Set<String> getQueryDependencies() {
        return queryDependencies;
    }

}
