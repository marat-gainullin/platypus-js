/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Parameters;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public interface ServerDataStorage {

    public int commit(List<Change> aLog, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception;
}
