package nl.tudelft.stocktrader.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SFLLogger {
	
	private static Logger logger = Logger.getLogger("SFLLogger");

	public static void logException(Exception ex){
		
		try {
			FileHandler handler = new FileHandler("E:/log/sfl_exception.log", true);
			logger.addHandler(handler);
			logger.setLevel(Level.ALL);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.log(Level.ALL, "sfl_exception: ", ex);
		
		
	}
}
