package org.dsher.kingbot.utils;

public class Utils {
	
	public static String getOrdinalSuffix(final int n) {
				
		int exceptionCheck = n % 100;
	    
	    if (exceptionCheck >= 11 && exceptionCheck <= 13) {
	        return "th";
	    }
	    switch (n % 10) {
	        case 1:  return "st";
	        case 2:  return "nd";
	        case 3:  return "rd";
	        default: return "th";
	    }
	}

}
