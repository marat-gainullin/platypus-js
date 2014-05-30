package com.eas.client.xhr.impl;

import com.eas.client.xhr.UrlQueryProcessor;

public class NoopUrlQueryProcessor implements UrlQueryProcessor{

	@Override
    public String process(String aQuery) {
	    return aQuery;
    }
	
}
