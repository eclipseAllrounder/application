package net.application.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.flow.Flow;
import javax.faces.flow.FlowScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.TreeNode;
import javax.transaction.Transactional;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.Role;
import org.richfaces.component.UITree;
//import org.richfaces.event.TreeSelectionChangeEvent;

import org.richfaces.event.TreeSelectionChangeEvent;

import net.application.authorization.annotation.Admin;
import net.application.authorization.util.IDMInitializer;
import net.application.authorization.util.IDMUtil;
import net.application.customer.entity.Customer;
import net.application.customer.util.GroupTreeBean;

import java.io.Serializable;


//@ManagedBean 
//@FlowScoped("idmUtilCreateGroup")
//@SessionScoped
@Named
@ConversationScoped
@Admin
public class IdmUtilCreateController implements Serializable {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    @Inject private FacesContext facesContext;
    
    @Inject PartitionManager partitionManager;
    public PartitionManager getPartitionManager() {
		return partitionManager;
	}
	@Inject IdentityManager identityManager;
    @Inject RelationshipManager relationshipManager;
    @Inject private Conversation conversation;
    @Inject private UserManagementController userMangementController;
    @Inject transient Logger log;
    
   
    private GroupTreeBean groupTreeBean;
 
    @Inject
    private IDMUtil ia;
    
    private String newGroupName;
    private String newRoleName;
	private String selectedGroupName;
    private String selectedSubGroupName;
    private String selectedSubSubGroupName;
    private String wantRootLevel="yes";
    private Boolean userDetailsGridFlag;
    private TreeNode currentSelection = null; 
    private TreeNode currentSelectionParent = null; 
    private TreeNode currentSelectionGrand = null; 
    private Integer currentSelectionChildCount;
   
    public void initConversation(){
		 log.info("call initConversation()");
		 
		// groupTreeBean=new GroupTreeBean();
		// groupTreeBean.setIdentityManager(identityManager);
		// groupTreeBean.init();
    	if (conversation.isTransient()) {
    		 log.info("conversation isTransient idmUtil Group initialized");
    	        conversation.begin();
    	        init();
    	}
    	if (!FacesContext.getCurrentInstance().isPostback()
    			&& conversation.isTransient()) {
    		 log.info("conversation idmUtil Group initialized");
    	       conversation.begin();
    	        init();
    	}
    }
    public String endConversation(){
    	if(!conversation.isTransient()){
    		conversation.end();
    		log.info("conversation idmUtil Group ended");
    	}
    	return "sucess";
	}
    public String next(){
    	return "secondpage?faces-redirect=true";
	}
    
	public Conversation getConversation() {
		return conversation;
    }
    
	public void initialize() {
	   Flow flow = FacesContext.getCurrentInstance().getApplication().getFlowHandler().getCurrentFlow();
	   Map<Object, Object> flowScope = FacesContext.getCurrentInstance().getApplication().getFlowHandler().getCurrentFlowScope();
	   log.info("Flow idmUtil initialized");
	}
		   
		   
	@PostConstruct
	public void init() {
	   log.info("Setting default Values");
	   wantRootLevel="yes";
	   currentSelectionChildCount = 0;
	   groupTreeBean=new GroupTreeBean();
	   groupTreeBean.setIdentityManager(identityManager);
	   groupTreeBean.init();
	   
	}
	

	 public void finalize() {
		 //create
	     log.info("Flow finalized");
	 }
	 
	 public void createGroup() {
	 try {
		 //IdentityManager identityManager = this.partitionManager.createIdentityManager();
		 if ((groupTreeBean.getSubGroupName()==null) && (groupTreeBean.getGroupName()!=null)){			 
			 Group group = new Group(groupTreeBean.getGroupName());
			 identityManager.add(group);
		 }
		 if ((groupTreeBean.getSubGroupName()!=null) && (groupTreeBean.getGroupName()!=null)){			 
			 Group group = new Group(groupTreeBean.getGroupName());
			 identityManager.add(group);
		     Group subgroup = new Group(groupTreeBean.getSubGroupName(), group);
		     identityManager.add(subgroup);
		 }
		
			
		 } catch (Exception e) {
	         String message = "An error has occured while creating" +
	                          " the group (see log for details)";
	         facesContext.addMessage(null, new FacesMessage(message));
	     }
	 }
	   public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) { 
	        // considering only single selection 
	    	System.out.println("currentSelection detected");
	    	groupTreeBean.selectionChanged(selectionChangeEvent);
	    	System.out.println("111111111111111");
	    	currentSelection=groupTreeBean.getCurrentSelection();
	    	System.out.println("222222222222222");
	    	currentSelectionParent=groupTreeBean.getCurrentSelectionParent();
	    	System.out.println("333333333333333");
	    	currentSelectionGrand=groupTreeBean.getCurrentSelectionGrand();
	    	System.out.println("444444444444444");
	    	currentSelectionChildCount=groupTreeBean.getCurrentSelectionChildCount();
	    	System.out.println("555555555555555");
	    } 
    @Admin
	public String createNewRootGroup() {
		ia.createNewRootGroup(newGroupName);
		endConversation();
		userMangementController.init();
		groupTreeBean.init();
		//return "success";
		log.info("erledigt");
    	return "/secure/administration/userAdministration?faces-redirect=true";
	}
   public String createNewGroup() {
	   //is root-level, edit second
   		if ((currentSelection!=null)&&(currentSelectionParent==null)&&(currentSelectionGrand==null)){
   			ia.createNewGroup(2, currentSelection.toString(), groupTreeBean.getSubGroupName());
   		}
   	    //is second level, edit third level
   		if ((currentSelection!=null)&&(currentSelectionParent!=null)&&(currentSelectionGrand==null)){
   			ia.createNewGroup(3, currentSelection.toString(), groupTreeBean.getSubSubGroupName());
   		}
   		userMangementController.init();
   		groupTreeBean.init();
   	    //zurücksetzen des Trees in der UserManagement-View
		userMangementController.getTreeBean().init();
   		endConversation();
    	return "/secure/administration/userAdministration?faces-redirect=true";
	}

   @Admin
 	public String createNewRole() {
 		ia.createNewRole(newRoleName);
 		endConversation();
 		userMangementController.init();
 		groupTreeBean.init();
 		//return "success";
 		log.info("erledigt");
     	return "/secure/administration/userAdministration?faces-redirect=true";
 	}

	public String getSelectedGroupName() {
		return selectedGroupName;
	}


	public void setSelectedGroupName(String selectedGroupName) {
		this.selectedGroupName = selectedGroupName;
	}


	public String getSelectedSubGroupName() {
		return selectedSubGroupName;
	}


	public void setSelectedSubGroupName(String selectedSubGroupName) {
		this.selectedSubGroupName = selectedSubGroupName;
	}


	public String getWantRootLevel() {
		return wantRootLevel;
	}


	public void setWantRootLevel(String wantRootLevel) {
		log.info("Set value: " + wantRootLevel);
		this.wantRootLevel = wantRootLevel;
	}
	
	public void loadYesNo(ValueChangeEvent e){
		//assign new value to localeCode
		log.info("Event value: " + e.getNewValue().toString());
		wantRootLevel = e.getNewValue().toString();

		}
	
	public Boolean getUserDetailsGridFlag() {
		return userDetailsGridFlag;
	}


	public void setUserDetailsGridFlag(Boolean userDetailsGridFlag) {
		this.userDetailsGridFlag = userDetailsGridFlag;
	}
	
	public String getSelectedSubSubGroupName() {
		return selectedSubSubGroupName;
	}
	public void setSelectedSubSubGroupName(String selectedSubSubGroupName) {
		this.selectedSubSubGroupName = selectedSubSubGroupName;
	}
	public GroupTreeBean getGroupTree() {
		return groupTreeBean;
	}
	public void setGroupTree(GroupTreeBean groupTreeBean) {
		this.groupTreeBean = groupTreeBean;
	}
	public Integer getCurrentSelectionChildCount() {
		return currentSelectionChildCount;
	}
	public void setCurrentSelectionChildCount(Integer currentSelectionChildCount) {
		this.currentSelectionChildCount = currentSelectionChildCount;
	}
	
	public String getNewGroupName() {
		return newGroupName;
	}
	public void setNewGroupName(String newGroupName) {
		this.newGroupName = newGroupName;
	}
	public GroupTreeBean getGroupTreeBean() {
		return groupTreeBean;
	}
	public void setGroupTreeBean(GroupTreeBean groupTreeBean) {
		this.groupTreeBean = groupTreeBean;
	}	
    public String getNewRoleName() {
		return newRoleName;
	}
	public void setNewRoleName(String newRoleName) {
		this.newRoleName = newRoleName;
	}

}
