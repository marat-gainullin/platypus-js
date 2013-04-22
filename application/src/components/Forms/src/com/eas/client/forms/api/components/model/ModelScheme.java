/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.client.forms.api.Component;
import com.eas.dbcontrols.scheme.DbScheme;

/**
 *
 * @author mg
 */
public class ModelScheme extends Component<DbScheme> {

    protected ModelScheme(DbScheme aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelScheme() {
        super();
        setDelegate(new DbScheme());
    }
}
