package net.application.web.util;

import java.util.List;

import net.application.customer.entity.Customer;
import net.application.web.entity.Mailserver;


public interface MailserverDao {
    
	public Mailserver getByName(String name);
	public Mailserver getByApplication(String application);
	public void deleteByApplication(String application);
	public List<Mailserver> getAllMailservers();
	public void createMailserver(Mailserver mailserver);
}