package com.example.pdv.tekupay;

public class cvmObject {

	public String description;
	public String condition;
	public String b7;
	public String shortName;
	
	public cvmObject(){
		
	}
	
	public void setCVM(String description, String condition, String b7, String shortName){
		this.description = description;
		this.condition = condition;
		this.b7 = b7;
		this.shortName = shortName;
	}
	
}
