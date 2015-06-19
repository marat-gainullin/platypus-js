/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.changes.Change;
import com.eas.script.Scripts;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public interface ServerDataStorage {

    public int commit(List<Change> aLog, Scripts.Space aSpace, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception;
}
