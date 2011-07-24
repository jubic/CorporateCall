package sg.edu.rp.c345.a02.corporatecall;

import android.os.Bundle;

public class Contacts {

	// Variables
	long id;
	String name;
	String function;
	String company;
	String phone;
	
	// Constructor
	public Contacts(long id, String name, String function, String company,
			String phone) {
		super();
		this.id = id;
		this.name = name;
		this.function = function;
		this.company = company;
		this.phone = phone;
	}
	
	// Construct from Bundle
	public Contacts(Bundle b) {
		name = b.getString("contactName");
		function = b.getString("contactFunc");
		company = b.getString("contactComp");
		phone = b.getString("contactNumb");
	}

	// A mapping from String values to various Parcelable types.
	// Used for passing data between various Activities.
	// A special type-safe container, called Bundle, is available for key/value
	// maps of different values.
	// Convert into bundle
	public Bundle getBundle() {
		Bundle bundle = new Bundle();
		
		bundle.putString("contactName", name);
		bundle.putString("contactFunc", function);
		bundle.putString("contactComp", company);
		bundle.putString("contactNumb", phone);
		
		
		return bundle;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return name;
	}
}
