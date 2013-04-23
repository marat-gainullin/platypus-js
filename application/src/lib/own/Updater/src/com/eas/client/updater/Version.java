/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.util.StringTokenizer;

/**
 *
 * @author AB
 */
public class Version {

    private int major = 0;
    private int minor = 0;
    private int build = 0;

    public Version(String ver) {
        StringTokenizer st = new StringTokenizer(ver, ".");
        int cnt = 0;
        while (st.hasMoreTokens()) {
            switch (cnt) {
                case 0: {
                    this.major = Integer.parseInt(st.nextToken());
                    break;
                }
                case 1: {
                    this.minor = Integer.parseInt(st.nextToken());
                    break;
                }
                case 2: {
                    this.build = Integer.parseInt(st.nextToken());
                    break;
                }
                default: {
                    String nextToken = st.nextToken();
                }
            }
            cnt++;
        }
    }

    /**
     * 
     * @param obj
     * @return
     */
    public int compareTo(Version obj) {
        if (obj == null) {
            return UpdaterConstants.FATAL_NOT_EQUALS;
        } else {
            if (this.major > obj.getMajor()) {
                return UpdaterConstants.FATAL_NOT_EQUALS;
            } else {
                if (this.minor > obj.getMinor()) {
                    return UpdaterConstants.NOT_EQUALS;
                } else {
                    if (this.build > obj.getBuild()) {
                        return UpdaterConstants.NOT_EQUALS;
                    } else {
                        return UpdaterConstants.EQUALS;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + build;
    }

    /**
     * @return the major
     */
    public int getMajor() {
        return major;
    }

    /**
     * @param major the major to set
     */
    public void setMajor(int major) {
        this.major = major;
    }

    /**
     * @return the minor
     */
    public int getMinor() {
        return minor;
    }

    /**
     * @param minor the minor to set
     */
    public void setMinor(int minor) {
        this.minor = minor;
    }

    /**
     * @return the build
     */
    public int getBuild() {
        return build;
    }

    /**
     * @param build the build to set
     */
    public void setBuild(int build) {
        this.build = build;
    }
}
