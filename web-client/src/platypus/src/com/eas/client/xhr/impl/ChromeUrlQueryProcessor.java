package com.eas.client.xhr.impl;

import com.eas.client.IdGenerator;
import com.eas.client.PlatypusHttpRequestParams;
import com.eas.client.xhr.UrlQueryProcessor;

/**
 * Workaround chrome bug 266971.
 * When it will be fixed, this class should be eliminated
 * @author mg
 *
 */
public class ChromeUrlQueryProcessor implements UrlQueryProcessor{

	@Override
    public String process(String aQuery) {
		return aQuery + (aQuery.isEmpty() ? "?" : "&") + PlatypusHttpRequestParams.CACHE_BUSTER + "=" + IdGenerator.genId();
    }
	
}
