package net.application.customer.util;

import java.io.IOException;
import java.io.Serializable; 
import java.util.ArrayList; 
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap; 
import java.util.Iterator;
import java.util.List; 
import java.util.Map; 
import java.util.Map.Entry;

import javax.annotation.PostConstruct; 
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.bean.ManagedBean; 
import javax.faces.bean.ManagedProperty; 
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped; 
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.TreeNode; 

import org.ajax4jsf.model.DataComponentState;
import org.ajax4jsf.model.DataVisitor;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.query.IdentityQuery;
import org.richfaces.cdi.push.Push;
import org.richfaces.component.AbstractTree;
import org.richfaces.component.UITree; 
import org.richfaces.component.UITreeNode;
import org.richfaces.event.TreeSelectionChangeEvent; 
import org.richfaces.event.TreeToggleEvent;
import org.richfaces.model.SequenceRowKey;


  

/** 
 * @author Christian Lewer
 */
@Named
public class GroupTreeBean implements Serializable { 
	
    
	private static final long serialVersionUID = 1L; 
    
 
    @Inject PartitionManager partitionManager;
    private IdentityManager identityManager;
   
    
  

	private List<TreeNode> rootNodes = new ArrayList<TreeNode>(); 
    private Map<String, net.application.customer.util.Group> groupCache = new HashMap<String, net.application.customer.util.Group>(); 
    private Map<String, SubGroup> subGroupCache = new HashMap<String, SubGroup>(); 
    private Map<String, SubSubGroup> subSubGroupCache = new HashMap<String, SubSubGroup>(); 
    private TreeNode currentSelection = null; 
    private TreeNode currentSelectionParent = null; 
    private TreeNode currentSelectionGrand = null; 
    private Integer currentSelectionChildCount = 0; 
    private Boolean expanded;
    private String groupName;
    private String subGroupName;
    private String subSubGroupName;
    private String groupNamePath;
    private String subGroupNamePath;
    private String subSubGroupNamePath;
   
	private Boolean disableGroupName;
    private Boolean disableSubGroupName;
    private Boolean disableSubSubGroupName;
    private Boolean showGroupName;
    private Boolean showSubGroupName;
    private Boolean showSubSubGroupName;
    
    private List<Group> groupsListToAdd;
    private List<Group> groupsListToDelete;
    
	//public static final String PUSH_FIRST_LEVEL_NAME = "pushGroupTreeFirstLevel";
    //public static final String PUSH_SECOND_LEVEL_NAME = "pushGroupTreeSecondLevel";
    //public static final String PUSH_THIRD_LEVEL_NAME = "pushGroupTreeThirdLevel";
    //@Inject
    //@Push(topic = PUSH_FIRST_LEVEL_NAME) Event<String> pushEventFirstLevelName;
    //@Inject
    //@Push(topic = PUSH_SECOND_LEVEL_NAME) Event<String> pushEventSecondLevelName;
    //@Inject
    //@Push(topic = PUSH_THIRD_LEVEL_NAME) Event<String> pushEventThirdLevelName;
   
    private String selectedDeleteNodeName;
  
    @PostConstruct
    public void init() { 
    	System.out.println("init Tree");
    	if(identityManager==null)identityManager = partitionManager.createIdentityManager();
    	//Switch for normal left, add or delete
    	rootNodes = new ArrayList<TreeNode>(); 
        groupCache = new HashMap<String, net.application.customer.util.Group>(); 
        subGroupCache = new HashMap<String, SubGroup>(); 
        subSubGroupCache = new HashMap<String, SubSubGroup>(); 
        deleteTreeSelection();
        currentSelection = null; 
        currentSelectionParent = null; 
        currentSelectionGrand = null; 
        currentSelectionChildCount = 0;
        groupName="Text eingeben";
 	    subGroupName="Text eingeben";
 	    subSubGroupName="Text eingeben";
 	    groupNamePath="Bitte Gruppe auswählen";
	  
 	    disableGroupName=true;
	    disableSubGroupName=true;
	    disableSubSubGroupName=true;
	    showGroupName=true;
	    showSubGroupName=true;
	    showSubSubGroupName=true;
    	IdentityQuery<Group> query = identityManager.<Group> createIdentityQuery(Group.class);
    	query.setParameter(Group.ENABLED, true);
    	// find only by the first name
    	List<Group> result = query.getResultList();
    	//System.out.println("size of all found groups is " + result.size());
    	String pathString = "vnull";
    	if (groupsListToAdd!=null) {
    		//System.out.println("groupsListToAdd != null and Size is " + groupsListToAdd.size());	
    		//only new groups
    		if (groupsListToAdd.size()>=0) {	
    			Boolean gefu=false;
	    	    //only groups with membership 
	    	    for (Group group : result) {
	    	    	gefu=false;
	    	    	for (Group groupSearch : groupsListToAdd) {
	    	    		pathString=groupSearch.getPath();
	    	    		if (pathString.matches(group.getPath()))gefu=true;
	    	    	}
	    	    	if(!gefu) {	
					   	//System.out.println(group.getPath());
					   	String[]parts=group.getPath().split("/");
					   	//System.out.println(parts.length);
					   	if(parts.length==2){
					   		//System.out.println("Group= "+ parts[1]);	
					   		net.application.customer.util.Group groupTree = getGroupByName(parts[1]);
					   	}
					   	if(parts.length==3){
					   		//System.out.println("Group= "+ parts[1] + " SubGroup= "+ parts[2]);	
					   		net.application.customer.util.Group groupTree = getGroupByName(parts[1]); 
					        SubGroup subGroup = getSubGroupByName(parts[2], groupTree); 
					   	}
					   	if(parts.length==4){
					   		//System.out.println("Group= "+ parts[1] + " SubGroup= "+ parts[2] + " SubSubGroup= "+ parts[3]);	
					   		net.application.customer.util.Group groupTree = getGroupByName(parts[1]); 
					        SubGroup subGroupTree = getSubGroupByName(parts[2], groupTree); 
					        SubSubGroup subSubGroup = getSubSubGroupByName(parts[3], groupTree, subGroupTree); 
					   	}
			    	}
				}
    		}
    	}
    	
    	if (groupsListToDelete!=null) {
    		//System.out.println("groupsListToDelete != null and Size is " + groupsListToDelete.size());	
    		if (groupsListToDelete.size()>=0) {	
	    		Boolean gefu=false;
	    	    //only groups with membership 
	    	    for (Group group : result) {
	    	    	gefu=false;
	    	    	for (Group groupSearch : groupsListToDelete) {
	    	    		pathString=groupSearch.getPath();
	    	    		if (pathString.matches(group.getPath()))gefu=true;
	    	    	}
	    	    	if(gefu) {	
				    	//System.out.println("search for " +group.getPath());
				    	String[]parts=group.getPath().split("/");
				    	//System.out.println(parts.length);
				    	if(parts.length==2){
				    		//System.out.println("Group= "+ parts[1]);	
				    		net.application.customer.util.Group groupTree = getGroupByName(parts[1]);
				    	}
				    	if(parts.length==3){
				    		//System.out.println("Group= "+ parts[1] + " SubGroup= "+ parts[2]);	
				    		net.application.customer.util.Group groupTree = getGroupByName(parts[1]); 
				            SubGroup subGroup = getSubGroupByName(parts[2], groupTree); 
				    	}
				    	if(parts.length==4){
				    		//System.out.println("Group= "+ parts[1] + " SubGroup= "+ parts[2] + " SubSubGroup= "+ parts[3]);	
				    		net.application.customer.util.Group groupTree = getGroupByName(parts[1]); 
				            SubGroup subGroupTree = getSubGroupByName(parts[2], groupTree); 
				            SubSubGroup subSubGroup = getSubSubGroupByName(parts[3], groupTree, subGroupTree); 
				    	}
		    		}
		    	}
    		}
    	}
    	
    	try {
    		if (groupsListToDelete.size()==0 && groupsListToAdd.size()==0) {	
    			//System.out.println("groupsListToDelete and groupsListToDelete size is 0" );	
    			for (Group group : result) {
    		    	//System.out.println(group.getPath());
    			   	String[]parts=group.getPath().split("/");
    			   	//System.out.println(parts.length);
    			   	if(parts.length==2){
    			  		//System.out.println("Group= "+ parts[1]);	
    			   		net.application.customer.util.Group groupTree = getGroupByName(parts[1]);
    			   	}
    			  	if(parts.length==3){
    			  	    //System.out.println("Group= "+ parts[1] + " SubGroup= "+ parts[2]);	
    			  		net.application.customer.util.Group groupTree = getGroupByName(parts[1]); 
    			  	    SubGroup subGroup = getSubGroupByName(parts[2], groupTree); 
    			   	}
    			  	if(parts.length==4){
    			   		//System.out.println("Group= "+ parts[1] + " SubGroup= "+ parts[2] + " SubSubGroup= "+ parts[3]);	
    			   		net.application.customer.util.Group groupTree = getGroupByName(parts[1]); 
    			   	    SubGroup subGroupTree = getSubGroupByName(parts[2], groupTree); 
    			   	    SubSubGroup subSubGroup = getSubSubGroupByName(parts[3], groupTree, subGroupTree); 
    			   	}
    		    }
    		}
    	} catch(Exception e) {
			//System.out.println("Add- and Delete-Tree null Exception");
			//throw new RuntimeException(e);
		}
    	if (groupsListToDelete==null && groupsListToAdd==null) {
			//System.out.println("groupsListToDelete and groupsListToDelete is null" );	
		    for (Group group : result) {
		    	//System.out.println(group.getPath());
			   	String[]parts=group.getPath().split("/");
			   	//System.out.println(parts.length);
			   	if(parts.length==2){
			  		//System.out.println("Group= "+ parts[1]);	
			   		net.application.customer.util.Group groupTree = getGroupByName(parts[1]);
			   	}
			  	if(parts.length==3){
			  		//System.out.println("Group= "+ parts[1] + " SubGroup= "+ parts[2]);	
			  		net.application.customer.util.Group groupTree = getGroupByName(parts[1]); 
			  	    SubGroup subGroup = getSubGroupByName(parts[2], groupTree); 
			   	}
			  	if(parts.length==4){
			   		//System.out.println("Group= "+ parts[1] + " SubGroup= "+ parts[2] + " SubSubGroup= "+ parts[3]);	
			   		net.application.customer.util.Group groupTree = getGroupByName(parts[1]); 
			   	    SubGroup subGroupTree = getSubGroupByName(parts[2], groupTree); 
			   	    SubSubGroup subSubGroup = getSubSubGroupByName(parts[3], groupTree, subGroupTree); 
			   	}
		    }
		}
      

    
    } 
   
    protected org.richfaces.component.UITree treeBinding;
    private Map<TreeNode, Boolean> toggleState = new HashMap<TreeNode, Boolean>(); 
    
    public org.richfaces.component.UITree getTreeBinding() {
        return treeBinding;
    }
 
    public void setTreeBinding(
            org.richfaces.component.UITree treeBinding) {
        this.treeBinding = treeBinding;
    }
    public void set(
            org.richfaces.component.UITree treeBinding) {
        this.treeBinding = treeBinding;
    }
    
      
    // And a method to expand/collapse any node programatically  
    public void setToggeStateForNode(TreeNode node, boolean expanded)  
    {  
          toggleState.put(node, expanded);  
    } 
  
     
    
    public void  doSelectNode0(String name){
    	//System.out.println("doSelectNode0:  "+ name);
    }
    
    public void processTreeToggle(TreeToggleEvent selectionChangeEvent) throws AbortProcessingException
    {
        // considering only single selection
        UITree tree = (UITree) selectionChangeEvent.getSource();
        NamedNode node = (NamedNode) tree.getRowData();
        node.setExpanded(tree.isExpanded());
       //System.out.println("toggelListener in action");
        
    }
    private void expandCollapse(TreeNode node, boolean expand) {
      
      if (node.children() != null && node.children().hasMoreElements()) {
            Enumeration childs = node.children();
            while (childs.hasMoreElements()) {
                expandCollapse((TreeNode) childs.nextElement(), expand);
            }
        }
    }
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) { 
        // considering only single selection 
    	List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
    	if (selection.size()>0){
	    	Object currentSelectionKey = selection.get(0);
	    	AbstractTree tree = (AbstractTree) selectionChangeEvent.getSource();
	    	Object storedKey = tree.getRowKey();
	    	tree.setRowKey(currentSelectionKey);
	    	
	    	currentSelection = (TreeNode) tree.getRowData();
	       	Collection<Object> selections = tree.getSelection();
	       	
	    	tree.setRowKey(storedKey);  
	    	
	    	
	    	 
	        
	        currentSelectionParent=null;
	        currentSelectionGrand=null;
	       	setCurrentSelectionChildCount(currentSelection.getChildCount());
	     
	        if(currentSelection.getParent()!=null){
	        	currentSelectionParent=currentSelection.getParent();
	        	if(currentSelectionParent.getParent()!=null){
	        		currentSelectionGrand=currentSelectionParent.getParent();
	        	}
	        }
	        if ((currentSelection!=null)&&(currentSelectionParent==null)&&(currentSelectionGrand==null)){
	    		
	        	disableGroupName=true;
	    		disableSubGroupName=false;
	    		disableSubSubGroupName=true;
	    		showGroupName=false;
	    		showSubGroupName=true;
	    		showSubSubGroupName=true;
	    		groupName=currentSelection.toString();
	    		groupNamePath="/" + groupName;
	    		subGroupName="Text eingeben";
	    		subSubGroupName="Text eingeben";
	    		//pushEventFirstLevelName.fire(groupName);
	    		//pushEventSecondLevelName.fire(subGroupName);
	    		//pushEventThirdLevelName.fire(subSubGroupName);
	    	}
	    	//is second level, edit third level
	    	if ((currentSelection!=null)&&(currentSelectionParent!=null)&&(currentSelectionGrand==null)){
	    		disableGroupName=true;
	    		disableSubGroupName=true;
	    		disableSubSubGroupName=false;
	    		showGroupName=false;
	    		showSubGroupName=false;
	    		showSubSubGroupName=true;
	    		groupName=currentSelectionParent.toString();
	    		subGroupName=currentSelection.toString();
	    		subSubGroupName="Text eingeben";
	    		groupNamePath="/" + groupName + "/" + subGroupName;
	    		//pushEventFirstLevelName.fire(groupName);
	    		//pushEventSecondLevelName.fire(subGroupName);
	    		//pushEventThirdLevelName.fire(subSubGroupName);
	    	}
	    	//is third level, edit nothing
	    	if ((currentSelection!=null)&&(currentSelectionParent!=null)&&(currentSelectionGrand!=null)){
	    		disableGroupName=true;
	    		disableSubGroupName=true;
	    		disableSubSubGroupName=true;
	    		showGroupName=false;
	    		showSubGroupName=false;
	    		showSubSubGroupName=false;
	    		groupName=currentSelectionGrand.toString();
	    		subGroupName=currentSelectionParent.toString();
	    		subSubGroupName=currentSelection.toString();
	    		groupNamePath="/" + groupName + "/" + subGroupName + "/" + subSubGroupName;
	    		//pushEventFirstLevelName.fire(groupName);
	    		//pushEventSecondLevelName.fire(subGroupName);
	    		//pushEventThirdLevelName.fire(subSubGroupName);
	    	}
	    	//nothing selected, edit nothing
	    	if ((currentSelection==null)&&(currentSelectionParent==null)&&(currentSelectionGrand==null)){
	    		disableGroupName=true;
	    		disableSubGroupName=true;
	    		disableSubSubGroupName=true;
	    		showGroupName=true;
	    		showSubGroupName=true;
	    		showSubSubGroupName=true;
	    		groupName="Text eingeben";
	    		subGroupName="Text eingeben";
	    		subSubGroupName="Text eingeben";
	    		//pushEventFirstLevelName.fire(groupName);
	    		//pushEventSecondLevelName.fire(subGroupName);
	    		//pushEventThirdLevelName.fire(subSubGroupName);
	    	}
    	}
       ////System.out.println("currentSelection:  "+ currentSelection);
        //System.out.println("currentSelection Parent:  "+ currentSelectionParent);
       ////System.out.println("currentSelection Grand:  "+ currentSelectionGrand);
        //System.out.println("currentSelection ChildCount:  "+ currentSelectionChildCount);
    } 
    public void deleteTreeSelection() {
		setGroupName("");
		setSubGroupName("");
		setSubSubGroupName("");	
		showGroupName=false;
	    showSubGroupName=false;
		showSubSubGroupName=false;
    }

    public Boolean adviseNodeSelected(UITree tree) {  
    	 if (currentSelection.equals((String)tree.getRowData())) {  
    		//System.out.println("adviseNodeSelected false: " + currentSelection);
    	     return false;  
    	 } else {  
    	     return false;  
    	 }  
    	 }
    private net.application.customer.util.Group getGroupByName(String groupName) { 
    	//System.out.println("GroupCache:  "+ groupName);
    	net.application.customer.util.Group group = groupCache.get(groupName); 
        if (group == null) { 
            group = new net.application.customer.util.Group(); 
            group.setName(groupName); 
            groupCache.put(groupName, group); 
            rootNodes.add(group); 
        } 
        return group; 
    } 
  
    private SubGroup getSubGroupByName(String subGroupName, net.application.customer.util.Group group) { 
        SubGroup subGroup = subGroupCache.get(subGroupName); 
        if (subGroup == null) { 
        	subGroup = new SubGroup(); 
        	subGroup.setName(subGroupName); 
        	subGroup.setParent(group); 
        	group.getSubGroups().add(subGroup); 
            subGroupCache.put(subGroupName, subGroup); 
        } 
        if (subGroup != null && !subGroup.getParent().equals(group)) { 
        	subGroup = new SubGroup(); 
        	subGroup.setName(subGroupName); 
        	subGroup.setParent(group); 
        	group.getSubGroups().add(subGroup); 
            subGroupCache.put(subGroupName, subGroup); 
        } 
        return subGroup; 
    } 
    private SubSubGroup getSubSubGroupByName(String subSubGroupName, net.application.customer.util.Group group, SubGroup subGroup) { 
        SubSubGroup subSubGroup = subSubGroupCache.get(subSubGroupName); 
        if (subSubGroup == null) { 
        	subSubGroup = new SubSubGroup(); 
        	subSubGroup.setName(subSubGroupName); 
        	subSubGroup.setParent(subGroup); 
        	subGroup.getSubSubGroups().add(subSubGroup); 
            subSubGroupCache.put(subSubGroupName, subSubGroup); 
        } 
        if (subSubGroup != null && subSubGroup.getParent()==null) { 
        	subSubGroup = new SubSubGroup(); 
        	subSubGroup.setName(subSubGroupName); 
        	subSubGroup.setParent(subGroup); 
        	subGroup.getSubSubGroups().add(subSubGroup); 
            subSubGroupCache.put(subSubGroupName, subSubGroup); 
        } 
        return subSubGroup; 
    } 
   
  
    public List<TreeNode> getRootNodes() { 
        return rootNodes; 
    } 
  
    public void setRootNodes(List<TreeNode> rootNodes) { 
        this.rootNodes = rootNodes; 
    } 
  
    public TreeNode getCurrentSelection() { 
    	//System.out.println("treeBean currentSelection:  "+ currentSelection);
        return currentSelection; 
    } 
  
    public void setCurrentSelection(TreeNode currentSelection) { 
        this.currentSelection = currentSelection; 
    }
	public TreeNode getCurrentSelectionParent() {
		return currentSelectionParent;
	}
	public void setCurrentSelectionParent(TreeNode currentSelectionParent) {
		this.currentSelectionParent = currentSelectionParent;
	}
	public TreeNode getCurrentSelectionGrand() {
		return currentSelectionGrand;
	}
	public void setCurrentSelectionGrand(TreeNode currentSelectionGrand) {
		this.currentSelectionGrand = currentSelectionGrand;
	}
	public Integer getCurrentSelectionChildCount() {
		return currentSelectionChildCount;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public String getSubGroupName() {
		return subGroupName;
	}


	public void setSubGroupName(String subGroupName) {
		this.subGroupName = subGroupName;
	}
	
	public String getSubSubGroupName() {
		return subSubGroupName;
	}
	public void setSubSubGroupName(String subSubGroupName) {
		this.subSubGroupName = subSubGroupName;
	}
	public void setCurrentSelectionChildCount(Integer currentSelectionChildCount) {
		this.currentSelectionChildCount = currentSelectionChildCount;
	}
	public Boolean getExpanded() {
		return expanded;
	}
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}
	public Boolean getDisableGroupName() {
		return disableGroupName;
	}
	public void setDisableGroupName(Boolean disableGroupName) {
		this.disableGroupName = disableGroupName;
	}
	public Boolean getDisableSubGroupName() {
		return disableSubGroupName;
	}
	public void setDisableSubGroupName(Boolean disableSubGroupName) {
		this.disableSubGroupName = disableSubGroupName;
	}
	public Boolean getDisableSubSubGroupName() {
		return disableSubSubGroupName;
	}
	public void setDisableSubSubGroupName(Boolean disableSubSubGroupName) {
		this.disableSubSubGroupName = disableSubSubGroupName;
	}
	public Boolean getShowGroupName() {
		return showGroupName;
	}
	public void setShowGroupName(Boolean showGroupName) {
		this.showGroupName = showGroupName;
	}
	public Boolean getShowSubGroupName() {
		return showSubGroupName;
	}
	public void setShowSubGroupName(Boolean showSubGroupName) {
		this.showSubGroupName = showSubGroupName;
	}
	public Boolean getShowSubSubGroupName() {
		return showSubSubGroupName;
	}
	public void setShowSubSubGroupName(Boolean showSubSubGroupName) {
		this.showSubSubGroupName = showSubSubGroupName;
	}
	 public String getGroupNamePath() {
			return groupNamePath;
	}
	public void setGroupNamePath(String groupNamePath) {
		this.groupNamePath = groupNamePath;
	}

	public List<Group> getGroupsListToAdd() {
		return groupsListToAdd;
	}
	public void setGroupsListToAdd(List<Group> groupsListToAdd) {
		this.groupsListToAdd = groupsListToAdd;
	}
	public List<Group> getGroupsListToDelete() {
		return groupsListToDelete;
	}
	public void setGroupsListToDelete(List<Group> groupsListToDelete) {
		this.groupsListToDelete = groupsListToDelete;
	}

	public String getSelectedDeleteNodeName() {
		return selectedDeleteNodeName;
	}

	public void setSelectedDeleteNodeName(String selectedDeleteNodeName) {
		this.selectedDeleteNodeName = selectedDeleteNodeName; 
		
		AbstractTree tree = (AbstractTree) treeBinding;
		
		
		

    	
   List<UIComponent> childList = tree.getChildren(); 

   
 
  
    	
		
		
	     
	     currentSelectionParent=null;
	     currentSelectionGrand=null;
	     setCurrentSelectionChildCount(currentSelection.getChildCount());
	    
        if(currentSelection.getParent()!=null){
        	currentSelectionParent=currentSelection.getParent();
        	if(currentSelectionParent.getParent()!=null){
        		currentSelectionGrand=currentSelectionParent.getParent();
        	}
        }
        if ((currentSelection!=null)&&(currentSelectionParent==null)&&(currentSelectionGrand==null)){
    		
        	disableGroupName=true;
    		disableSubGroupName=false;
    		disableSubSubGroupName=true;
    		showGroupName=false;
    		showSubGroupName=true;
    		showSubSubGroupName=true;
    		groupName=currentSelection.toString();
    		groupNamePath="/" + groupName;
    		subGroupName="Text eingeben";
    		subSubGroupName="Text eingeben";
    		//pushEventFirstLevelName.fire(groupName);
    		//pushEventSecondLevelName.fire(subGroupName);
    		//pushEventThirdLevelName.fire(subSubGroupName);
    	}
    	//is second level, edit third level
    	if ((currentSelection!=null)&&(currentSelectionParent!=null)&&(currentSelectionGrand==null)){
    		disableGroupName=true;
    		disableSubGroupName=true;
    		disableSubSubGroupName=false;
    		showGroupName=false;
    		showSubGroupName=false;
    		showSubSubGroupName=true;
    		groupName=currentSelectionParent.toString();
    		subGroupName=currentSelection.toString();
    		subSubGroupName="Text eingeben";
    		groupNamePath="/" + groupName + "/" + subGroupName;
    		//pushEventFirstLevelName.fire(groupName);
    		//pushEventSecondLevelName.fire(subGroupName);
    		//pushEventThirdLevelName.fire(subSubGroupName);
    	}
    	//is third level, edit nothing
    	if ((currentSelection!=null)&&(currentSelectionParent!=null)&&(currentSelectionGrand!=null)){
    		disableGroupName=true;
    		disableSubGroupName=true;
    		disableSubSubGroupName=true;
    		showGroupName=false;
    		showSubGroupName=false;
    		showSubSubGroupName=false;
    		groupName=currentSelectionGrand.toString();
    		subGroupName=currentSelectionParent.toString();
    		subSubGroupName=currentSelection.toString();
    		groupNamePath="/" + groupName + "/" + subGroupName + "/" + subSubGroupName;
    		//pushEventFirstLevelName.fire(groupName);
    		//pushEventSecondLevelName.fire(subGroupName);
    		//pushEventThirdLevelName.fire(subSubGroupName);
    	}
    	//nothing selected, edit nothing
    	if ((currentSelection==null)&&(currentSelectionParent==null)&&(currentSelectionGrand==null)){
    		disableGroupName=true;
    		disableSubGroupName=true;
    		disableSubSubGroupName=true;
    		showGroupName=true;
    		showSubGroupName=true;
    		showSubSubGroupName=true;
    		groupName="Text eingeben";
    		subGroupName="Text eingeben";
    		subSubGroupName="Text eingeben";
    		//pushEventFirstLevelName.fire(groupName);
    		//pushEventSecondLevelName.fire(subGroupName);
    		//pushEventThirdLevelName.fire(subSubGroupName);
    	}
	}


	  public IdentityManager getIdentityManager() {
			return identityManager;
		}

		public void setIdentityManager(IdentityManager identityManager) {
			this.identityManager = identityManager;
		}
	

}
