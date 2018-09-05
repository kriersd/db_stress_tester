package com.ibm.icp.coc.dbstress;

public class Datum {
	
	public long timestamp = 0;
	public long runTime = 0;
	
	public Datum() {
		
	}
	
	public Datum(long timestamp, long runTime ) {
		this.timestamp = timestamp;
		this.runTime = runTime;
	}

}
