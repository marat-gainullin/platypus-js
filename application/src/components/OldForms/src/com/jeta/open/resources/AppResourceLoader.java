/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jeta.open.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import com.jeta.open.registry.JETARegistry;
import java.awt.Color;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class is an implementation of a ResourceLoader.  It insulates
 * the application code from having any need to know about the
 * local file system directory structure.  It is also useful for debugging and
 * development so we can redirect resource request to debug files if needed.
 * @author    Jeff Tassin
 */
public class AppResourceLoader implements ResourceLoader {

    private ClassLoader m_classloader;
    /** an empty icon if a resource cannot be loaded */
    private static ImageIcon m_empty_icon = new ImageIcon();
    /** cache of images */
    private static HashMap<String, ImageIcon> m_imagecache = new HashMap<String, ImageIcon>();

    public AppResourceLoader() {
    }

    /**
     * @return a custom class loader for the application
     */
    @Override
    public ClassLoader getClassLoader() {
        if (m_classloader == null) {
            return AppResourceLoader.class.getClassLoader();
        } else {
            return m_classloader;
        }
    }

    /**
     * Returns an icon with a red X to show an icon that could not be loaded.
     */
    public static ImageIcon getEmptyIcon() {
        synchronized (AppResourceLoader.class) {
            if (m_empty_icon == null) {
                int width = 16;
                int height = 16;
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D bg = img.createGraphics();
                Color controlColor = javax.swing.UIManager.getColor("control");
                if (controlColor == null) {
                    controlColor = Color.white;
                }
                bg.setColor(controlColor);
                bg.fillRect(0, 0, width, height);
                bg.setColor(java.awt.Color.red);
                bg.drawRect(0, 0, width - 1, height - 1);
                bg.drawLine(0, 0, width - 1, height - 1);
                bg.drawLine(0, height - 1, width - 1, 0);
                bg.dispose();
                m_empty_icon = new ImageIcon(img);
            }
        }
        return m_empty_icon;
    }

    /**
     * Opens and returns an input stream for the given resourceName.   The
     * resourceName is relative to the application CLASSPATH (i.e. JAR file).
     * @param resourceName the relative name of the resource to open
     * @return an input stream for the given resourceName.
     */
    @Override
    public InputStream getResourceAsStream(String resourceName) throws IOException {
        ClassLoader classloader = getClassLoader();
        return classloader.getResourceAsStream(resourceName);
    }

    public static ImageIcon load(String imageName) {
        return getImage(imageName);
    }

    /**
     * Loads an image from disk. The image is loaded relative to the application directory.
     * @todo we need to cache these images
     */
    public static ImageIcon getImage(String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            ImageIcon icon = m_imagecache.get(imageName);
            if (icon == null) {
                try {
                    ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
                    if (loader != null) {
                        icon = loader.loadImage(imageName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (icon == null) {
                    icon = new ImageIcon();
                }
                m_imagecache.put(imageName, icon);
            }
            return icon;
        } else {
            //assert( false );
            return m_empty_icon;
        }
    }

    /**
     * Helper utility to load an image file from the application images directory
     * @param imageName the name (and optional sub directory ) of the file to load
     */
    public static URL getImageUrl(String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            imageName = imageName.replace('\\', '/');
            try {
                ClassLoader classloader = AppResourceLoader.class.getClassLoader();
                // try to lookup simple resource
                java.net.URL url = classloader.getResource(imageName);
                // try to lookup relative resource
                if (url == null) {
                    url = classloader.getResource(RtIcons.m_iconsPrefix + imageName);
                }
                return url;
            } catch (Exception e) {
                System.err.println("AppResourceLoader.loadImage failed: imageName: " + imageName + " Errormessage: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Helper utility to load an image file from the application images directory
     * @param imageName the name (and optional sub directory ) of the file to load
     */
    @Override
    public ImageIcon loadImage(String imageName) {
        URL imageUrl = getImageUrl(imageName);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        return getEmptyIcon();
    }

    @Override
    public void setClassLoader(ClassLoader loader) {
        m_classloader = loader;
    }

    public String[] listFiles(String fname, String regexFilter) throws IOException {
        ClassLoader classloader = getClassLoader();
        java.net.URL url = classloader.getResource(fname);

        if (url != null) {
            try {

                File dir = new File(url.toURI());

                Pattern pat = null;
                try {
                    pat = Pattern.compile(regexFilter);
                } catch (PatternSyntaxException e) {
                    throw new IllegalArgumentException(regexFilter);
                }

                final Pattern pattern = pat;
                String[] filenames = dir.list(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        Matcher matcher = pattern.matcher(name);
                        if (matcher.find()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                if (filenames == null) {
                    return new String[0];
                } else {
                    return filenames;
                }
            } catch (URISyntaxException ex) {
                Logger.getLogger(AppResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
                return new String[0];
            }
        } else {
            return new String[0];
        }
    }
}
