package com.ibm.icp.coc.dbstress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Test extends Thread {
	private static final String SQL_INSERT = "INSERT INTO todos (owner, item) VALUES (?, ?)";
	private static final String SQL_SELECT = "SELECT * FROM todos WHERE owner=? AND item=?";
	private static final String SQL_DELETE = "DELETE FROM todos WHERE owner=? AND item=?";
	private List<String> items = new ArrayList<String>();
	private String testerId = null;
	private StressTestMain tester = null;
	private boolean canceled = false;
	private int runSize = 20;

	public Test(StressTestMain tester, int runSize) {
		UUID uuid = UUID.randomUUID();
		this.testerId = uuid.toString();
		this.items = new ArrayList<String>();
		this.tester = tester;
	}

	@Override
	public void run() {
		
		System.out.println( Thread.currentThread().getName() + " starting.");

		while (!canceled) {
			items = new ArrayList<String>();

			try {
				Thread.sleep((long)(Math.random() * 1000));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			long startTime = System.currentTimeMillis();

			for (int i = 0; (i < runSize && !canceled); i++) {
				try {
					String item = insertRow();
					items.add(item);
				} catch (SQLException e) {
					String msg = Thread.currentThread().getName() + ": " + e.getMessage();
					StressTestMain.INSTANCE.log(msg);
					//e.printStackTrace();
				}
			}
			
			// select each 
			for (String item : items) {
				try {
					selectRow(item);
				} catch (SQLException e) {
					String msg = Thread.currentThread().getName() + ": " + e.getMessage();
					StressTestMain.INSTANCE.log(msg);
					//e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep((long)(Math.random() * 1000));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// now remove them
			while (!items.isEmpty()) {
				try {
					deleteRow();
				} catch (SQLException e) {
					String msg = Thread.currentThread().getName() + ": " + e.getMessage();
					StressTestMain.INSTANCE.log(msg);
					//e.printStackTrace();
				}
			}
			
			long endTime = System.currentTimeMillis();

			long runTime = endTime - startTime;
			
			tester.addTime(endTime, runTime);

		}
		System.out.println( Thread.currentThread().getName() + " done.");
	}

	public void cancel() {
		this.canceled = true;
	}

	private void deleteRow() throws SQLException {
		Connection connection = Database.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			String item = items.get(0);
			
			preparedStatement = connection.prepareStatement(SQL_DELETE);
			preparedStatement.setString(1, testerId);
			preparedStatement.setString(2, item);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			items.remove(0);
			
		} finally {
			if ( preparedStatement != null && !preparedStatement.isClosed() ) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed() ) {
				connection.close();
			}
		}
	}

	private void selectRow(String item) throws SQLException {
		Connection connection = Database.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			preparedStatement.setString(1, testerId);
			preparedStatement.setString(2, item);
			ResultSet result = preparedStatement.executeQuery();
			if( result.next() ) {
				String foundItem = result.getString(2);
				if( !item.equals(foundItem) ) {
					String msg = Thread.currentThread().getName() + ": INVALID item: " + item;
					StressTestMain.INSTANCE.log(msg);
				} else {
					
				}
			} else {
				String msg = Thread.currentThread().getName() + ": MISSING item: " + item;
				StressTestMain.INSTANCE.log(msg);
			}
			preparedStatement.close();
			
		} finally {
			if ( preparedStatement != null && !preparedStatement.isClosed() ) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed() ) {
				connection.close();
			}
		}
	}

	private String insertRow() throws SQLException {
		Connection connection = Database.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			UUID uuid = UUID.randomUUID();
			String newItem = uuid.toString();
			items.add(newItem);
			
			preparedStatement = connection.prepareStatement(SQL_INSERT);
			preparedStatement.setString(1, testerId);
			preparedStatement.setString(2, newItem);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			
			return newItem;
			
		} finally {
			if ( preparedStatement != null && !preparedStatement.isClosed() ) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed() ) {
				connection.close();
			}
		}
	}
}
