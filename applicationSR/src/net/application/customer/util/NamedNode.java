package net.application.customer.util;

import java.io.Serializable; 
public class NamedNode implements Serializable { 
	protected String type; 
	protected String name; 
	private Boolean expanded;
	public String getName() { 
	return name; 
	} 
	public void setName(String name) { 
	this.name = name; 
	} 
	public String getType() { 
	return type; 
	} 
	public void setType(String type) { 
	this.type = type; 
	} 
	@Override
	public String toString() { 
	return this.name; 
	}
	public Boolean getExpanded() {
		return expanded;
	}
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	} 
}
