package net.application.web.util;

import java.util.List;

import net.application.customer.entity.Customer;
import net.application.web.entity.ApplicationControl;
import net.application.web.entity.Mailserver;


public interface ApplicationControlDao {
    

	public void deleteAllApplicationControls();
	public List<ApplicationControl> getAllApplicationControls();
	public void createApplicationControl(ApplicationControl applicationControl);
}