/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.RequestHandler;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class ResourceRequestHandler extends RequestHandler<ResourceRequest, ResourceRequest.Response> {

    public ResourceRequestHandler(PlatypusServerCore aServerCore, ResourceRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Session aSession, Consumer<ResourceRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        String relativeName = getRequest().getResourceName();
        relativeName = relativeName.replace("\\", File.separator);
        relativeName = relativeName.replace("/", File.separator);
        Path resolved = serverCore.getIndexer().getAppPath().resolve(relativeName);
        File resourceFile = resolved.toFile();
        if (resourceFile.exists()) {
            try {
                Date serverResourceTime = new Date(resourceFile.lastModified());
                Date clientResourceTime = getRequest().getTimeStamp();
                ResourceRequest.Response resp = new ResourceRequest.Response();
                if (clientResourceTime == null || serverResourceTime.after(clientResourceTime)) {
                    resp.setContent(FileUtils.readBytes(resourceFile));
                    resp.setTimeStamp(serverResourceTime);
                }
                try {
                    onSuccess.accept(resp);
                } catch (Exception ex) {
                    Logger.getLogger(ResourceRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                if (ex instanceof FileNotFoundException) {
                    onFailure.accept(new FileNotFoundException(relativeName));
                } else {
                    onFailure.accept(ex);
                }
            }
        } else {
            onFailure.accept(new FileNotFoundException(relativeName));
        }
    }

}
