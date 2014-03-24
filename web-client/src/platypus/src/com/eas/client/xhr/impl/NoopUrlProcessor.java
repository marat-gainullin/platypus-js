package com.eas.client.xhr.impl;

import com.eas.client.xhr.UrlProcessor;

public class NoopUrlProcessor implements UrlProcessor{

	@Override
    public String process(String aUrl) {
	    return aUrl;
    }
	
}
