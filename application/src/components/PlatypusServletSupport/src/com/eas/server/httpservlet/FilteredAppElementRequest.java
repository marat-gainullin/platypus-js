/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.server.filter.AppElementsFilter;

/**
 * For filtered app element, like browser-like modules.
 *
 * @author mg
 */
public class FilteredAppElementRequest extends AppElementRequest {

    public FilteredAppElementRequest(String aAppElementId) {
        super(-1L, aAppElementId);
    }

    public static class FilteredResponse extends com.eas.client.threetier.Response {

        protected AppElementsFilter.FilteredXml filtered;
        protected String scriptPath;

        public FilteredResponse(AppElementsFilter.FilteredXml aFiltered, String aScriptPath) throws Exception {
            super(-1L);
            filtered = aFiltered;
            scriptPath = aScriptPath;
        }

        public String getScriptPath() {
            return scriptPath;
        }

        public String getFilteredContent() {
            return filtered.content;
        }

        @Override
        public void accept(PlatypusResponseVisitor prv) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
