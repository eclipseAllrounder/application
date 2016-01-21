package net.application.web.util;

import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import net.application.web.controller.IdmUtilCreateController;


@ManagedBean
@RequestScoped
public class DropDownMenuBean {
	@Inject transient Logger log;
	@Inject  IdmUtilCreateController iUCC;
	public String switchToGroup(){
		log.info("Navigation to Group");
		//return "toGroup";
		iUCC.endConversation();
		iUCC.initConversation();
		return "/secure/administration/utilCreateGroup.jsf?faces-redirect=true";
	 }
	public String switchToRole(){
		log.info("Navigation to Role");
		//return "toGroup";
		iUCC.endConversation();
		iUCC.initConversation();
		return "/secure/administration/utilCreateRole.jsf?faces-redirect=true";
	 }
}
