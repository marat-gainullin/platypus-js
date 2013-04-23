package com.bearsoft.rowset.dataflow;

public interface TransactionListener {

    public interface Registration {

        public void remove();
    }

    public void commited() throws Exception;

    public void rolledback() throws Exception;
}