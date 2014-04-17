package com.bearsoft.gwt.ui.widgets.grid.header;

import java.util.ArrayList;
import java.util.List;

public class HeaderSplitter {

	// settings
	protected int minLeave;
	protected int maxLeave;
	// processing
	protected int leaveIndex = -1;
	
	protected HeaderSplitter(int aMinLeave, int aMaxLeave){
		super();
		minLeave = aMinLeave;
		maxLeave = aMaxLeave;
	}
	
	public static List<HeaderNode> split(List<HeaderNode> toBeSplitted, int aMinLeave, int aMaxLeave){
		HeaderSplitter splitter = new HeaderSplitter(aMinLeave, aMaxLeave);
		List<HeaderNode> result = new ArrayList<>();
		splitter.process(toBeSplitted, result, null);
		return result;
	}
	
	protected void process(List<HeaderNode> toBeSplitted, List<HeaderNode> result, HeaderNode aClonedParent){
		for(int i = 0; i < toBeSplitted.size(); i++){
			if(leaveIndex == maxLeave - 1)
				break;
			HeaderNode n = toBeSplitted.get(i);
			HeaderNode nc = new HeaderNode(n.getHeader());
			nc.setStyle(n.getStyle());
			if(n.getChildren().isEmpty()){
				leaveIndex++;
			}else{
				process(n.getChildren(), nc.getChildren(), nc);
			}
			if(leaveIndex >= minLeave && leaveIndex < maxLeave){
				result.add(nc);
				nc.setParent(aClonedParent);
			}
		}
	}
}
