package com.eas.grid.columns.header;

import java.util.List;


public class HeaderAnalyzer<T> {

	protected int depth;
	
	protected HeaderAnalyzer(){
		super();
	}
	
	public static <T> void analyze(List<HeaderNode<T>> aForest){
		HeaderAnalyzer<T> analyzer = new HeaderAnalyzer<T>();
		analyzer.maxDepth(aForest, 0);
		analyzer.mine(aForest, 0, null);
	}
	
	protected void maxDepth(List<HeaderNode<T>> aForest, int aDepth){
		aDepth++;
		if(depth < aDepth)
			depth = aDepth;
		for(int i = 0; i < aForest.size(); i++){
			HeaderNode<T> n = aForest.get(i);
			if(!n.getChildren().isEmpty()){
				maxDepth(n.getChildren(), aDepth);
			}
		}
	}
	
	protected int mine(List<HeaderNode<T>> aForest, int aDepth, HeaderNode<T> aParent){
		aDepth++;
		int leavesCount = 0;
		for(int i = 0; i < aForest.size(); i++){
			HeaderNode<T> n = aForest.get(i);
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
	
    public static <T> void achieveLeaves(List<HeaderNode<T>> aRoots, List<HeaderNode<T>> aLeaves) {
        for (HeaderNode<T> node : aRoots) {
            if (node.isLeaf()) {
                aLeaves.add(node);
            } else {
                achieveLeaves(node.getChildren(), aLeaves);
            }
        }
    }

}
