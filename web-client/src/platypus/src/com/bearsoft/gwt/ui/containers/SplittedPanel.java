/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gwt.ui.containers;

import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * 
 * @author mg
 */
public class SplittedPanel extends SplitLayoutPanel {

	@Override
	protected void onAttach() {
		super.onAttach();
		forceLayout();// GWT animations are deprecated because of browser's
					  // transitions
	}

}
