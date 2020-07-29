package org.dsher.kingbot.utils;

public class Utils {

	public static String capitalize(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static boolean isNumeric(String s) {
		if (s == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static int getRandom(int min, int max) {
		return (int) ((Math.random() * ((max - min) + 1)) + min);
	}

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
