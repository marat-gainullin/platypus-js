/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.changes.*;
import com.eas.client.DatabasesClient;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pk, mg refactoring
 */
public class CommitRequestHandler extends SessionRequestHandler<CommitRequest> {

    public CommitRequestHandler(PlatypusServerCore server, Session session, CommitRequest rq) {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception {
        Map<String, List<Change>> changeLogs = new HashMap<>();
        DatabasesClient client = getServerCore().getDatabasesClient();
        Map<String, SqlCompiledQuery> entities = new HashMap<>();
        List<Change> changes = getRequest().getChanges();
        for (Change change : changes) {
            SqlCompiledQuery entity = entities.get(change.entityId);
            if (entity == null) {
                SqlQuery query = client.getAppQuery(change.entityId, false);
                if (!query.isPublicAccess()) {
                    throw new AccessControlException(String.format("Public access to query entity %s is denied while commiting changes for that entity.", change.entityId));//NOI18N
                }
                entity = query.compile();
                entities.put(change.entityId, entity);
            }
            if(change instanceof Command){
                ((Command)change).command = entity.getSqlClause();
            }
            List<Change> targetChangeLog = changeLogs.get(entity.getDatabaseId());
            if(targetChangeLog == null){
                targetChangeLog = new ArrayList<>();
                changeLogs.put(entity.getDatabaseId(), targetChangeLog);
            }
            targetChangeLog.add(change);
        }
        int updated = client.commit(changeLogs);
        return new CommitRequest.Response(getRequest().getID(), updated);
    }
}
