package com.eas.client.xhr;

import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * A ready-state callbacks for an {@link XMLHttpRequest} object. You should make
 * a new AbstractProgressHandler instead
 */
public interface ProgressHandler {

	/**
	 * This is called whenever the request starts. See
	 * {@link XMLHttpRequest#setOnLoadStart}.
	 * 
	 * @param xhr
	 *            the object whose state has changed.
	 */
	void onLoadStart(XMLHttpRequest xhr);

	/**
	 * This is called whenever sending and loading data. See
	 * {@link XMLHttpRequest#setOnProgress}.
	 * 
	 * @param xhr
	 *            the object whose state has changed.
	 */
	void onProgress(ProgressEvent evt);

	/**
	 * This is called whenever the request has been aborted. For instance, by
	 * invoking the abort() method. See {@link XMLHttpRequest#setOnAbort()}.
	 * 
	 * @param xhr
	 *            the object whose state has changed.
	 */
	void onAbort(XMLHttpRequest xhr);

	/**
	 * This is called whenever the request has failed. See
	 * {@link XMLHttpRequest#setOnError}.
	 * 
	 * @param xhr
	 *            the object whose state has changed.
	 */
	void onError(XMLHttpRequest xhr);

	/**
	 * This is called whenever the request has successfully completed. See
	 * {@link XMLHttpRequest#setOnLoad}.
	 * 
	 * @param xhr
	 *            the object whose state has changed.
	 */
	void onLoad(XMLHttpRequest xhr);

	/**
	 * This is called whenever the author specified timeout has passed before
	 * the request could complete.See {@link XMLHttpRequest#setOnTimeOut}.
	 * 
	 * @param xhr
	 *            the object whose state has changed.
	 */
	void onTimeOut(XMLHttpRequest xhr);

	/**
	 * This is called whenever the request has completed (either in success or
	 * failure). See {@link XMLHttpRequest#setOnLoadEnd}.
	 * 
	 * @param xhr
	 *            the object whose state has changed.
	 */
	void onLoadEnd(XMLHttpRequest xhr);

}