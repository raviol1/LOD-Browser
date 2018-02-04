
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class JDBC_Servlet
 */
@WebServlet("/JDBC_Servlet")
public class JDBC_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JDBC_Servlet() {
		super();
		if (connection == null) {
			try {
				Class.forName("org.postgresql.Driver");
				System.out.println("DRIVER FOUND.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			String url = "jdbc:postgresql://localhost/YAGO";
			Properties props = new Properties();
			props.setProperty("user", "tom");
			props.setProperty("password", "L4jocond4");
			props.setProperty("port", "5432");
			try {
				connection = DriverManager.getConnection(url, props);
				System.out.println("CONNECTION ESTABLISHED.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JSONObject json = new JSONObject();
		JSONArray nodes = new JSONArray();
		JSONArray links = new JSONArray();
		JSONObject ids = new JSONObject();
		JSONObject connects = new JSONObject();

		HashSet<String> hashNodes = new HashSet<String>();
		if (connection != null) {

			try {
				// emptying the previous dummy json
				json.clear();
				// this string should get the subject which the user typed in
				String subj = request.getParameterValues("subj")[0];
				String pred = request.getParameterValues("pred")[0];
				String obj = request.getParameterValues("obj")[0];
				String lim = request.getParameterValues("lim")[0];
				String dep = request.getParameterValues("dep")[0];

				Statement stmt = connection.createStatement();
				ResultSet rset = null;
				ResultSet rsetAdd = null;
				String queryS =   (subj != "") ? "SUBJ='" + subj + "'" 
								+ ((pred != "") ? " AND PRED='" + pred + "'" : "")
								+ ((obj != "") ? " AND OBJ='" + obj + "'" : "")
								+ ((lim != "") ? " LIMIT " + (Integer.valueOf(lim)+1) : " LIMIT 20")
									: "";
				System.out.println(queryS);

				rset = stmt.executeQuery("SELECT * FROM allfacts WHERE " + queryS);

				if (!rset.next()) {
					queryS = (subj != "") ? "SUBJ like '%" + subj + "%'" 
							+ ((pred != "") ? " AND PRED like '%" + pred + "%'" : "")
							+ ((obj != "") ? " AND OBJ like '%" + obj + "%'" : "")
							+ ((lim != "") ? " LIMIT " + (Integer.valueOf(lim)+1) : " LIMIT 20")
								: "";
					rset = stmt.executeQuery("SELECT * FROM allfacts WHERE " + queryS);
				}

				while (rset.next()) {

					hashNodes.add(rset.getString(1));
					hashNodes.add(rset.getString(3));

					// in this part i populate the links between the subjects and predicates

					connects.clear();
					connects.put("source", rset.getString(1));
					connects.put("target", rset.getString(3));
					connects.put("value", "8");
					links.add(connects);

					// I like to see the results in the console
					System.out.println(rset.getString(1) + "\t" + rset.getString(2) + "\t" + rset.getString(3));
				}

				// Lets populate a second resultset with additional nodes to have a more
				// extensive graph
				ArrayList<String> nodesRightNow = new ArrayList<>();
				ArrayList<String> pastNodes = new ArrayList<>();
				for (int z = 0; z < Integer.valueOf(dep); z++) {

					pastNodes.addAll(nodesRightNow);
					nodesRightNow.clear();
					nodesRightNow.addAll(hashNodes);
					nodesRightNow.removeAll(pastNodes);
					nodesRightNow.remove(subj);
					nodesRightNow.remove(obj);
					String query = "";
					nodesRightNow.remove("<United_States>");

					for (int i = 0; i < nodesRightNow.size(); i++) {
						if (i == 0) {
							query += "(";
						}
						if (i != 0) {
							query += ") UNION (";
						}
						query += "SELECT * FROM allfacts WHERE SUBJ='" + nodesRightNow.get(i) + "'" +
								((pred != "") ? (" AND PRED='" + pred + "'") : "")+ " LIMIT 10";
						if (i == nodesRightNow.size()-1) {
							query += ")";
						}
					}
					
					System.out.println(query+"");
					rsetAdd = stmt.executeQuery(query);

					while (rsetAdd.next()) {

						hashNodes.add(rsetAdd.getString(1));
						hashNodes.add(rsetAdd.getString(3));

						// in this part i populate the links between the subjects and objects

						connects.clear();
						connects.put("source", rsetAdd.getString(1));
						connects.put("target", rsetAdd.getString(3));
						connects.put("value", "2");
						links.add(connects);

						System.out.println(
								rsetAdd.getString(1) + "\t" + rsetAdd.getString(2) + "\t" + rsetAdd.getString(3));
					}

					rsetAdd.close();

				}

				// now comes the part where the content of the hashset is written to json as
				// id's
				ArrayList<String> nodeArray = new ArrayList<>();
				nodeArray.addAll(hashNodes);
				for (int i = 0; i < nodeArray.size(); i++) {
					ids.clear();
					ids.put("id", nodeArray.get(i) + "");
					ids.put("group", i);
					nodes.add(ids);
				}

				stmt.close();
				rset.close();

				// add ids and connections together to final json
				json.put("nodes", nodes);
				json.put("links", links);

				System.out.println("JOB DONE");
				// and return it to the html
				response.setContentType("application/json");
				response.getWriter().write(json.toString());

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// usually stays empty otherwise
		doGet(request, response);
	}

}
