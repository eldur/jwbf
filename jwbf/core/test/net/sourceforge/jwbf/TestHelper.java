package net.sourceforge.jwbf;

import java.util.Random;

public abstract class TestHelper {

	private Random wheel = new Random();
	
	public TestHelper() {
		super();
	}

	protected String getRandomAlph(int length) {
		return getRandom(length, 65, 90);
	}

	protected String getRandom(int length) {
		return getRandom(length, 48, 126);
	}

	protected String getRandom(int length, int begin, int end) {
		String out = "";
		int charNum = 0;
		int count = 1;  
		while (count <= length) {  
	        charNum = (wheel.nextInt(79) + begin);
	        if (charNum >= begin && charNum <= end) {
	        	
	        	char d = (char) charNum;
	            out += d;
	            count++;
	        }
	    }
		return out;
	}

}