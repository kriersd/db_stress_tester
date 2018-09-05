package com.ibm.icp.coc;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.icp.coc.dbstress.StressTestMain;


@WebServlet("/StartTest")
public class StartTest extends HttpServlet {
       
	private static final long serialVersionUID = -6144535289411178789L;


    public StartTest() {
        super();
    }


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if( !StressTestMain.INSTANCE.isRunning() ) {
			String host = request.getParameter("host");
			String dbname = request.getParameter("dbname");
			String user = request.getParameter("user");
			String pass = request.getParameter("pass");
			String noThreads = request.getParameter("threads");
			String runSizeStr = request.getParameter("runSize");
			
			int threads = Integer.parseInt(noThreads);
			int runSize = Integer.parseInt(runSizeStr);
			
			if( validHostname(host) 
					&& user!=null && user.length()>0 
					&& pass!=null && pass.length()>0 
					&& dbname!=null && dbname.length()>0 
					&& threads>0 && threads<=10 ) {
				try {
					StressTestMain.INSTANCE.startTests(threads, runSize, host, dbname, user, pass);
					response.setStatus(200);
				} catch (SQLException e) {
					e.printStackTrace();
					response.sendError(400, "Unable to intialize database.");
				}
			} else {
				response.sendError(400, "Invalid parameters");
			}
			
			
		} else {
			// return 
			response.sendError(400, "Tests are already running");
		}
		
	}
	
	private boolean validHostname(String host) {
		return (host != null && host.length()>0 );
	}

}
