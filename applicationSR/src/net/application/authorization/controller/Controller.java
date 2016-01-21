/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.application.authorization.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import net.application.authorization.annotation.Admin;
import net.application.authorization.annotation.Author;
import net.application.authorization.annotation.Customer;




import net.application.authorization.util.SendMailTLS;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketlink.Identity;
import org.picketlink.Identity.AuthenticationResult;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.credential.Credentials.Status;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.User;



/**
 * The secured controller restricts access to certain methods
 * 
 * @author Shane Bryzak
 * 
 */
// Expose the bean to EL

@Named
public class Controller {

	@Inject transient Logger log;
    @Inject private FacesContext facesContext;
 	@Inject private Identity identity;
    @Inject	DefaultLoginCredentials credentials;
    @Inject IdentityManager identityManager;
  
      
  
    public String logout() {
       identity.logout();
       FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
       return "/loggedOut.jsf?faces-redirect=true";
      
       
    }
    public String login() {
    	        
        
        Password plainTextPassword = new Password((String)credentials.getPassword());
		UsernamePasswordCredentials credential = new UsernamePasswordCredentials();
		credential.setUsername(credentials.getUserId());
	    credential.setPassword(plainTextPassword);
	  //block if status unverified
        User user = BasicModel.getUser(identityManager, credentials.getUserId());
        if (user.getAttribute("status")!=null){
	        if (user.getAttribute("status").getValue().toString().matches(net.application.util.Status.UNVERIFIED.toString())) {
	            FacesContext.getCurrentInstance().addMessage("loggi", new FacesMessage(
	                    "Authentication was unsuccessful.  Unverified!" + "before trying again."));
	            log.info("Authentication was unsuccessful.  Unverified!" + "before trying again.");
	            return "false";
	        }
        }
        
	    identityManager.validateCredentials(credential);
        log.info(credential.getStatus().toString());
        //SendMailTLS.test();
       
        
        //redirect if password is expired
        if (Status.EXPIRED.equals(credential.getStatus())) {
        	return "/loggedIn.jsf?faces-redirect=true";
        }
    	AuthenticationResult result = identity.login();
    	log.info(result.toString());
        if (AuthenticationResult.FAILED.equals(result)) {
            FacesContext.getCurrentInstance().addMessage("loggi", new FacesMessage(
                    "Authentication was unsuccessful.  Please check your username and password " + "before trying again."));
            return "false";
        }
        
        return "/secure/userLoggedIn/loggedIn.xhtml?faces-redirect=true";
    }
    public void clearMessages(){
    	FacesContext.getCurrentInstance().getMessages().remove();
    	Iterator<FacesMessage> iter = FacesContext.getCurrentInstance().getMessages();
    	while (iter.hasNext()) {
    //		iter.remove();
    	}
    }

    /**
     * Only users with the employee application role can invoke this method
     */
    @Customer
    public void employeeMethod() {
        facesContext.addMessage(null, new FacesMessage("You executed an @Employee method"));
    }

    /**
     * Only users with the admin application role can invoke this method
     */
    @Admin
    public void adminMethod() {
        facesContext.addMessage(null, new FacesMessage("You executed an @Admin method"));
    }

    public String getStackTrace() {
        Throwable throwable = (Throwable)  FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.exception");
        StringBuilder builder = new StringBuilder();
        builder.append(throwable.getMessage()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            builder.append(element).append("\n");
        }
        return builder.toString();
    }
}


