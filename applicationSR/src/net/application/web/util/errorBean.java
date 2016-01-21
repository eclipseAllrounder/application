package net.application.web.util;


import static org.picketlink.idm.model.basic.BasicModel.addToGroup;
import static org.picketlink.idm.model.basic.BasicModel.grantRole;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.flow.Flow;
import javax.faces.flow.FlowScoped;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;

import net.application.authorization.util.CreateUser;
import net.application.customer.entity.Country;
import net.application.customer.entity.Customer;
import net.application.customer.entity.PasswordTable;
import net.application.customer.util.CountryDao;
import net.application.customer.util.CustomerDao;
import net.application.customer.util.PasswordTableDao;
import net.application.web.controller.WebContentController;


@Named
@RequestScoped
public class errorBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject Logger log;
	@Inject private WebContentController webContentController;
	private String error;
	private String errorId;
   
	
	
	
	//@PostConstruct
	public void init() {
		log.info(errorId);
		error=webContentController.getContentTextByName(errorId);
		log.info(error);
	}	
	    
	 @PreDestroy 
	 public void onDestruction() {
		 log.info("destroyed");		  
	 }

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
		init();
	}


	
}
