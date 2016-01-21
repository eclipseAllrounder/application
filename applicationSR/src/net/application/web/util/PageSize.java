package net.application.web.util;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
@Model
public class PageSize {
	
	private Integer screenWidth;
	
	@Produces
    @Named
    public Integer getSreenWidth() {
        return screenWidth;
    }
	@PostConstruct
	public void calculateScreenSize(){
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    System.out.println("####### SreenSize Height: " + screenSize.getHeight());
	    System.out.println("####### SreenSize Width: " + screenSize.getWidth());
	    screenWidth=(int) screenSize.getWidth();
	    screenWidth=850+(screenWidth-850)*50/100;
	    System.out.println("####### New SreenSize Width: " + screenWidth);
	}


}