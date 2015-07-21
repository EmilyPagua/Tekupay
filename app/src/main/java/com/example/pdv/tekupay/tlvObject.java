package com.example.pdv.tekupay;

import android.os.Looper;
import android.widget.Toast;

public class tlvObject extends MainActivity {
	
	private final static String tags[] = {"9F26","9F42","9F44","9F05","5F25","5F24","94","4F","82","50","9F12","5A","5F34","87","9F3B","9F43","61","9F36","9F07","9F08","89","5F54","8C","8D","5F20","9F0B","8E","8F","83","9F27","9F45","84","9D","73","9F49","70","BF0C","A5","6F","9F4C","9F2D","9F2E","9F2F","9F46","9F47","9F48","5F53","9F0D","9F0E","9F0F","9F10","91","9F11","5F28","5F55","5F56","42","90","9F32","92","86","9F18","71","72","5F50","5F2D","9F13","9F4D","9F4F","9F14","9F4E","9F17","9F38","80","77","5F30","88","9F4B","93","9F4A","9F1F","9F20","57","97","99","9F23"};
	private final static String parentNodeTag[] = {"6F","A5","70","BF0C"};

	public static tagObject[] heapDataObject = new tagObject[70];
	public static int heapCounter = 0;
	
	public tlvObject(){
		
	}
	
	public static void processTLV(String tlv) {
		String tag = "";
		String tagLength = "";
		String tagValue = "";
		if(tlv.length() > 0){
			tlv = tlv.substring(0, tlv.length()-4);
			for(int i = 0; i < tlv.length(); i+=2){
				boolean isTag = false;
				String tagType = tlv.substring(i+1, i+2);
				String tagExceptionShort = tlv.substring(i, i+2);
				if(tagType.equals("F") && (!tagExceptionShort.equals("6F") && !tagExceptionShort.equals("4F") && !tagExceptionShort.equals("8F"))){
					tag = tlv.substring(i, i+4);
					i += 2;
				}
				else
					tag = tlv.substring(i, i+2);
				
				if(i+4 <= tlv.length()){
					if(tlv.substring(i+2, i+4).equals("81"))
						i += 2;
				}
					
				for(int j = 0; j < parentNodeTag.length; j++){
					if(tag.equals(parentNodeTag[j])){
						i += 2;
						break;
					}
					else if(j == parentNodeTag.length-1){
							
						for(int k = 0; k < tags.length; k++){
							if(tags[k].equals(tag))
								isTag = true;
						}
							
						if(!isTag){
							break;
						}
						
						else{
							i += 2;
							tagLength = tlv.substring(i, i+2);
							int length = Integer.parseInt(tagLength, 16);
							tagValue = tlv.substring(i+2, i+2+length*2);
								
							for(int k = 0; k < heapCounter; k++){
								if(heapDataObject[k].tagTLV.equals(tag)){
									new Thread() {
										@Override
										public void run() {
											Looper.prepare();
											Toast.makeText(MainActivity.mainContext, "Chip Inválido - Tag repetido", Toast.LENGTH_LONG).show();
							                Looper.loop();
							            }
							        }.start();
							        i = tlv.length(); //Finish the main loop
								}
							}
							//MainActivity.logText.append("tag "+tag+"\n"+tagValue+" \n\n");
							
							heapDataObject[heapCounter] = new tagObject();
							heapDataObject[heapCounter].tlvTagObject(tag, tagValue);
							heapCounter++;
							i += length*2;
						}
					}
				}
			}
		}
    }
	
	public static String findTLV(String tag){
		for(int i = 0; i < heapCounter; i++){
			if(heapDataObject[i].tagTLV.equals(tag))
				return heapDataObject[i].valueTLV;
		}
		return "";
	}
	
}
