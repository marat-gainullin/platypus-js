/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier.http;

import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Date;

/**
 *
 * @author mg
 */
public class PlatypusHttpResponseReader implements PlatypusResponseVisitor{

    protected HttpURLConnection conn;
    protected int responseCode;
    
    public PlatypusHttpResponseReader(HttpURLConnection aConn) throws IOException{
        super();
        conn = aConn;
        responseCode = conn.getResponseCode();
    }
    
    @Override
    public void visit(ErrorResponse rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(CredentialRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(LogoutRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(CreateServerModuleRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(CommitRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(ModuleStructureRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(ResourceRequest.Response rsp) throws Exception {
        conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            byte[] content = BinaryUtils.readStream(is, -1);
            long stamp = conn.getHeaderFieldDate(PlatypusHttpConstants.HEADER_LAST_MODIFIED, 0);
            rsp.setContent(content);
            rsp.setTimeStamp(stamp != 0 ? new Date(stamp) : null);
        }else if(responseCode == HttpURLConnection.HTTP_NOT_MODIFIED){
            rsp.setContent(null);
            rsp.setTimeStamp(null);
        }
    }

    @Override
    public void visit(AppQueryRequest.Response rsp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
