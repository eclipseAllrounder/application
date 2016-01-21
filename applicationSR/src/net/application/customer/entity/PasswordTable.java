package net.application.customer.entity;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@NamedQueries({
	  @NamedQuery(name="getAllPasswords", query="SELECT o FROM PasswordTable o")
})

public class PasswordTable implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Integer passwordId;
	private String password;
	
	@Id @GeneratedValue
	public Integer getPasswordId(){ return passwordId; }
	public void setPasswordId( Integer passwordId ){ this.passwordId = passwordId; }
	
	public String getPassword(){ return password; }
	public void setPassword(String password){ this.password = password; }
}
