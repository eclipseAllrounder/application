package net.application.web.entity;



import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;


@Entity
@NamedQueries({
	  @NamedQuery(name="findAllContents", query="SELECT o FROM Content o"),
	  @NamedQuery(name="findContentByWebsite", 
      query="SELECT o FROM Content o WHERE o.website = :website"),
	  @NamedQuery(name="findContentByName", 
      query="SELECT o FROM Content o WHERE o.name = :name"),
	  @NamedQuery(name="deleteContentByName", 
      query="DELETE FROM Content o WHERE o.name = :name")
})

@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Content implements Serializable
{

	/**
	 *
	 * @author Christian Lewer
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer contentId;
	private String name;
	private String website;
	private Integer position;
	
	private Boolean active;
	private Date dob;
	
	private byte[] text;
	
	 @Id @GeneratedValue
	 public Integer getContentId(){ return contentId; }
	 public void setContentId(Integer contentId) { this.contentId = contentId; }   

	@NotEmpty
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	@NotNull(message="{booleanYesNo.notNull}")
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	@Lob
	@Column(length=10000000)
	public byte[] getText() {
		return text;
	}
	public void setText(byte[] text) {
		this.text = text;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}


}
