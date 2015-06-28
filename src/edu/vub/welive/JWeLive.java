package edu.vub.welive;

/** 
 * Interface that the Java object implements with the methods that AmbientTalk objects call on it. 
 */

public interface JWeLive {
	
	public JWeLive registerATApp(ATWeLive weLive);
	
	public void fillCell(int row, int col, int ID);	
	public void clearCell(int row, int col);
	public void greyCell(int row, int col);
	
	public void displayLog(String log);
	
	
	// XXX: Test
	public void callJava(String arg);
	
}
