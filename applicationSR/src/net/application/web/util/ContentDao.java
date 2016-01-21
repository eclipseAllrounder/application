package net.application.web.util;

import java.util.List;

import net.application.web.entity.Content;



public interface ContentDao {
    
	public Content getByName(String name);
	public void deleteByName(String name);
	public List<Content> getAllContents();
	public List<Content> getAllContentsByWebsite();
	public void createContent(Content content);
}