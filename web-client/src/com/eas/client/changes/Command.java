package com.eas.client.changes;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mg
 */
public class Command extends Change {

	public String command;// transient property
    private final List<Value> parameters = new ArrayList<>();

	public Command(String aEntityName) {
		super(aEntityName);
	}

    public List<Value> getParameters() {
        return parameters;
    }
    
	@Override
	public void accept(ChangeVisitor aChangeVisitor) throws Exception {
		aChangeVisitor.visit(this);
	}
}
