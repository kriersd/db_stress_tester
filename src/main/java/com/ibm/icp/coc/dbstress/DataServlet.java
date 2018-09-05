package com.ibm.icp.coc.dbstress;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DataServlet
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/data" })
public class DataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Datum[] data = StressTestMain.INSTANCE.getData();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean needsComma = false;
		int index = 0;
		for (Datum datum : data) {
			if( needsComma ) {
				sb.append(",");
			} else {
				needsComma = true;
			}
			sb.append("{\"index\":" + index + ",\"runTime\":"+datum.runTime+"}");
			index++;
		}
		sb.append("]");
		
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		out.print(sb.toString());
		out.flush();
	}

}
