package com.eas.client;

public abstract class CumulativeRunnableAdapter extends RunnableAdapter{

	protected int exepectedExecutesCount;
	protected int executed;

	public CumulativeRunnableAdapter(int aExecutesCount) {
		super();
		exepectedExecutesCount = aExecutesCount;
	}

	protected abstract void doWork();
	
	public void run() {
		if (++executed == exepectedExecutesCount) {
			super.run();
		}
	}
}
