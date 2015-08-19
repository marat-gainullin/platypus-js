/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.designer.datamodel.nodes.ModelNode;

/**
 *
 * @author mg
 */
public interface ModelProvider {

    public ApplicationDbModel getModel() throws Exception;

    public ModelNode<ApplicationDbEntity, ApplicationDbModel> getModelNode() throws Exception;
    
}
