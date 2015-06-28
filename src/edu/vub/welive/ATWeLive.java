package edu.vub.welive;


// AT needs to implement so Java can talk to AT

public interface ATWeLive {
	
	public void callAT(String arg);
	
	public void touchedCell(int row, int col);
	//public void switchOnlineOffline();	// TODO in AT
}
