package net.application.web.entity;



import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.validator.constraints.NotEmpty;


@Entity
@NamedQueries({
	  @NamedQuery(name="findAllSkins", query="SELECT o FROM Skins o")
})

public class Skins implements Serializable
{

	/**
	 *
	 * @author Christian Lewer
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer skinId;
	private String name;

	
	@Id @GeneratedValue
	public Integer getSkinId() {
		return skinId;
	}
	public void setSkinId(Integer skinId) {
		this.skinId = skinId;
	}

	@NotEmpty
	 public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


}
