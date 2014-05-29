package com.bearsoft.gwt.ui.widgets.grid.header;

import java.util.List;

public class HeaderAnalyzer {

	protected int depth;
	
	protected HeaderAnalyzer(){
		super();
	}
	
	public static void analyze(List<HeaderNode> aForest){
		HeaderAnalyzer analyzer = new HeaderAnalyzer();
		analyzer.maxDepth(aForest, 0);
		analyzer.mine(aForest, 0, null);
	}
	
	protected void maxDepth(List<HeaderNode> aForest, int aDepth){
		aDepth++;
		if(depth < aDepth)
			depth = aDepth;
		for(int i = 0; i < aForest.size(); i++){
			HeaderNode n = aForest.get(i);
			if(!n.getChildren().isEmpty()){
				maxDepth(n.getChildren(), aDepth);
			}
		}
	}
	
	protected int mine(List<HeaderNode> aForest, int aDepth, HeaderNode aParent){
		aDepth++;
		int leavesCount = 0;
		for(int i = 0; i < aForest.size(); i++){
			HeaderNode n = aForest.get(i);
			if(!n.getChildren().isEmpty()){
				leavesCount += mine(n.getChildren(), aDepth, n);
			}else{
				n.depthRemainder = depth - aDepth;
				leavesCount += 1;
			}
		}
		if(aParent != null)
			aParent.leavesCount = leavesCount; 
		return leavesCount;
	}
}
