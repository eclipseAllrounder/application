package net.application.customer.util;

import java.util.ArrayList; 
import java.util.Enumeration; 
import java.util.List; 
import javax.swing.tree.TreeNode; 
import com.google.common.collect.Iterators; 

public class SubSubGroup extends NamedNode implements TreeNode { 
	
	private SubGroup subGroup; 
	
	public SubSubGroup() { 
		this.setType("subSubGroup"); 
	} 
	public TreeNode getChildAt(int childIndex) { 
		return null; 
	} 
	public int getChildCount() { 
		return 0; 
	} 
	public TreeNode getParent() { 
		return subGroup; 
	} 
	public void setParent(SubGroup subGroup) { 
	this.subGroup = subGroup; 
	} 
	public int getIndex(TreeNode node) { 
	return 0; 
	} 
	public boolean getAllowsChildren() { 
	return false; 
	} 
	public boolean isLeaf() { 
	return true; 
	} 
	public Enumeration<TreeNode> children() { 
		return new Enumeration<TreeNode>() { 
			public boolean hasMoreElements() { 
				return false; 
			} 
			public TreeNode nextElement() { 
				return null; 
			} 
		}; 
	}
	
	
}
