package com.example.pdv.tekupay;

public class tagObject{

	public String tag;
	public String value;
	public String minLength;
	public String maxLength;
	
	public String tagTLV;
	public String valueTLV;
	
	public tagObject(){
		
	}
	
	public tagObject(String tag, String minLength, String maxLength, String value){
		this.tag = tag;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.value = value;
	}
	
	public void tlvTagObject(String tag, String value){
		this.tagTLV = tag;
		this.valueTLV = value;
	}
}
