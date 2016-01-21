package net.application.customer.util;

import java.util.ArrayList; 
import java.util.Enumeration; 
import java.util.List; 
import javax.swing.tree.TreeNode; 
import com.google.common.collect.Iterators; 

public class Group extends NamedNode implements TreeNode { 

	private List<SubGroup> subGroups = new ArrayList<SubGroup>(); 
	public Group() { 
		this.setType("group"); 
	} 
	public TreeNode getChildAt(int childIndex) { 
		return subGroups.get(childIndex); 
	} 
	public int getChildCount() { 
		return subGroups.size(); 
	} 
	public TreeNode getParent() { 
		return null; 
	} 
	public int getIndex(TreeNode node) { 
		return subGroups.indexOf(node); 
	} 
	public boolean getAllowsChildren() { 
		return true; 
	} 
	public boolean isLeaf() { 
		return subGroups.isEmpty(); 
	} 
	public Enumeration<SubGroup> children() { 
		return Iterators.asEnumeration(subGroups.iterator()); 
	} 
	public List<SubGroup> getSubGroups() { 
		return subGroups; 
	} 
}
