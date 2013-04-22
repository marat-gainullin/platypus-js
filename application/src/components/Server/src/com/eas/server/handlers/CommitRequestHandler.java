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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pk, mg refactoring
 */
public class CommitRequestHandler extends SessionRequestHandler<CommitRequest> {

    protected static class ChangesApplier implements ChangeVisitor {

        protected List<Change> target;
        protected SqlCompiledQuery compiledEntity;

        public ChangesApplier(List<Change> aTarget, SqlCompiledQuery aEntity) {
            super();
            target = aTarget;
            compiledEntity = aEntity;
        }

        @Override
        public void visit(Insert aChange) throws Exception {
            target.add(aChange);
        }

        @Override
        public void visit(Update aChange) throws Exception {
            target.add(aChange);
        }

        @Override
        public void visit(Delete aChange) throws Exception {
            target.add(aChange);
        }

        @Override
        public void visit(Command aChange) throws Exception {
            target.add(aChange);
            aChange.command = compiledEntity.getSqlClause();
        }
    }

    public CommitRequestHandler(PlatypusServerCore server, Session session, CommitRequest rq) {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception {
        DatabasesClient client = getServerCore().getDatabasesClient();
        String sessionId = getSession().getId();
        Map<String, SqlCompiledQuery> entities = new HashMap<>();
        List<Change> changes = getRequest().getChanges();
        for (Change change : changes) {
            SqlCompiledQuery entity = entities.get(change.entityId);
            if (entity == null) {
                SqlQuery query = client.getQueryFactory().getQuery(change.entityId, false);
                if (!query.isPublicAccess()) {
                    throw new AccessControlException(String.format("Public access to query entity %s is denied while commiting changes for that entity.", change.entityId));//NOI18N
                }
                entity = query.compile();
                entities.put(change.entityId, entity);
            }
            List<Change> target = client.getChangeLog(entity.getDatabaseId(), sessionId);
            ChangesApplier applier = new ChangesApplier(target, entity);
            change.accept(applier);
        }
        int updated = client.commit(sessionId);
        return new CommitRequest.Response(getRequest().getID(), updated);
    }
}
