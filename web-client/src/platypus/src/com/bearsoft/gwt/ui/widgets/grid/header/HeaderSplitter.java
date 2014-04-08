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
			HeaderNode n = toBeSplitted.get(i);
			if(n.getChildren().isEmpty())
				leaveIndex++;
			if(leaveIndex >= minLeave && leaveIndex < maxLeave){
				HeaderNode nc = new HeaderNode();
				nc.setHeader(n.getHeader());
				result.add(nc);
				nc.setParent(aClonedParent);
				if(!n.getChildren().isEmpty()){
					process(n.getChildren(), nc.getChildren(), nc);
				}
			}else if(leaveIndex < minLeave){
				if(!n.getChildren().isEmpty()){
					process(n.getChildren(), null, null);
				}
			}else{
				break;
			}
		}
	}
}
