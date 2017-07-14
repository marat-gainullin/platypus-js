package com.eas.client.queries;

import com.eas.script.Scripts;
import java.util.function.Consumer;

/**
 *
 * @author mg
 * @param <Q>
 */
public interface QueriesProxy <Q extends Query> {
    
    public Q getQuery(String aName, Scripts.Space aSpace, Consumer<Q> onSuccess, Consumer<Exception> onFailure) throws Exception;
    
    public Q getCachedQuery(String aName);
}
