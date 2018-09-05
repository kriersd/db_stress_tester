package com.ibm.icp.coc.dbstress;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum StressTestMain {
	INSTANCE;
	
	private final String SQL_CREATE_DB = "CREATE DATABASE IF NOT EXISTS ";
	private final String SQL_USE_DB = "USE ";
	private final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS todos\n" + 
			"(`owner` VARCHAR(100) NOT NULL, `item` VARCHAR(100) NOT NULL);";
	
	private List<Test> threads;
	private ConcurrentLinkedQueue<Datum> latestData = new ConcurrentLinkedQueue<Datum>();
	private ConcurrentLinkedQueue<String> log = new ConcurrentLinkedQueue<String>();
	private boolean running = false;
	
	public void startTests(int noThreads, int runSize, String host, String database, String user, String pass) throws SQLException {
		running = true;
		Database.init(host, database, user, pass);
		
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(SQL_CREATE_DB + database + ';');
			statement.executeUpdate(SQL_USE_DB + database + ';');
			statement.executeUpdate(SQL_CREATE_TABLE);

		} finally {
			if ( statement != null && !statement.isClosed() ) {
				statement.close();
			}
			if (connection != null && !connection.isClosed() ) {
				connection.close();
			}
		}
			
		// lets find table if we can
		
		threads = new ArrayList<Test>();
		for(int i=0; i<noThreads; i++) {
			Test aTest = new Test(this, runSize);
			threads.add(aTest);
			aTest.start();
		}
	}

	public void stopTests() {
		if( threads!= null && threads.size()>0 ) {
			for (Test test : threads) {
				test.cancel();
			}
		}
		latestData.clear();
		log.clear();
		running = false;
	}
	
	synchronized public boolean isRunning() {
		return running;
	}
	
	synchronized public void addTime(long timestamp, long runTime ) {
		Datum data = new Datum(timestamp, runTime);
		if( latestData.size() >= 100 ) {
			latestData.remove();
		}
		latestData.add(data);
	}

	public Datum[] getData() {
		return latestData.toArray(new Datum[0]);
	}
	
	synchronized public void log(String msg ) {
		if( log.size() >= 100 ) {
			log.remove();
		}
		log.add(msg);
	}

	synchronized public String[] getLog() {
		return log.toArray(new String[0]);
	}
	
}
