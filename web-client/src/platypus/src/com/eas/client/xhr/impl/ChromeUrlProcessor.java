package com.eas.client.xhr.impl;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.PlatypusHttpRequestParams;
import com.eas.client.xhr.UrlProcessor;

/**
 * Wokrs aroun chrome bug 266971.
 * When it will be fixed, this class should be elimiated
 * @author mg
 *
 */
public class ChromeUrlProcessor implements UrlProcessor{

	@Override
    public String process(String aUrl) {
		return aUrl + "?" + PlatypusHttpRequestParams.CACHE_BUSTER + "=" + IDGenerator.genId();
    }
	
}
