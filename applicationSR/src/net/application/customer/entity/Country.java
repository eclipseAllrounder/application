package net.application.customer.entity;



import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.validator.constraints.NotEmpty;


@Entity
@NamedQueries({
	  @NamedQuery(name="findAllCountries", query="SELECT o FROM Country o")
})

public class Country implements Serializable
{

	/**
	 *
	 * @author Christian Lewer
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer countryId;
	private String name_de;
	private String continent_de;
	private String name_en;
	private String continent_en;

	
	
	
	@Id @GeneratedValue
	public Integer getCountryId() {
		return countryId;
	}
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}


	 public String getName_de() {
		return name_de;
	}
	public void setName_de(String name_de) {
		this.name_de = name_de;
	}
	
	public String getContinent_de() {
		return continent_de;
	}
	public void setContinent_de(String continent_de) {
		this.continent_de = continent_de;
	}
	
	public String getName_en() {
		return name_en;
	}
	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getContinent_en() {
		return continent_en;
	}
	public void setContinent_en(String continent_en) {
		this.continent_en = continent_en;
	}

//	   // Helpers ------------------------------------------------------------------------------------

//	   // This must return true for another Foo object with same key/id.
//	   public boolean equals(Object other) {
//	       return other instanceof Country && (countryId != null) ? countryId.equals(((Country) other).countryId) : (other == this);
//	   }

//	   // This must return the same hashcode for every Foo object with the same key.
//	   public int hashCode() {
//	       return countryId != null ? this.getClass().hashCode() + countryId.hashCode() : super.hashCode();
//	   }

//	   // Override Object#toString() so that it returns a human readable String representation.
//	   // It is not required by the Converter or so, it just pleases the reading in the logs.
//	   public String toString() {
//	       return "Country[" + countryId + "," + name_de + "," + name_de + "," + continent_de + "," + continent_en + "]";
//	   }
}
