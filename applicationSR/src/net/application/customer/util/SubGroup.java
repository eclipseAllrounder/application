package net.application.customer.util;

import java.util.ArrayList; 
import java.util.Enumeration; 
import java.util.List; 

import javax.swing.tree.TreeNode; 

import com.google.common.collect.Iterators; 

public class SubGroup extends NamedNode implements TreeNode { 
	private List<SubSubGroup> subSubGroups = new ArrayList<SubSubGroup>();
	private Group group; 
	
	public SubGroup() { 
		this.setType("subGroup"); 
	} 
	public TreeNode getChildAt(int childIndex) { 
		return subSubGroups.get(childIndex); 
	} 
	public int getChildCount() { 
		return subSubGroups.size(); 
	} 
	public TreeNode getParent() { 
		return group; 
	} 
	public void setParent(Group group) { 
	this.group = group; 
	} 
	public int getIndex(TreeNode node) { 
	return subSubGroups.indexOf(node); 
	} 
	public boolean getAllowsChildren() { 
	return true; 
	} 
	public boolean isLeaf() { 
	return subSubGroups.isEmpty(); 
	} 
	public Enumeration children() { 
	return Iterators.asEnumeration(subSubGroups.iterator()); 
	} 
	public Group getGroup() { 
	return group; 
	} 
	public void setGroup(Group group) { 
	this.group = group; 
	} 
	public List<SubSubGroup> getSubSubGroups() { 
	return subSubGroups; 
	} 
}
