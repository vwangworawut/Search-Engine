import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ExactSearchServlet extends LoginBaseServlet {

	private static final String TITLE = "Exact Search";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = getUsername(request);

		if (user != null) {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			out.printf("<div style=\"background:url(https://pbs.twimg.com/profile_images/1503726907/twitterlogo-usf-hex.png) white bottom right no-repeat scroll;border:0px solid black;width:900px;height:500px;font-size:18px;\">");
			
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");

			out.printf("<h1 align=\"center\">Exact Search</h1>%n%n");

			out.printf("<form action =\"searchresults\">%n");
			out.printf("<input type=\"text\" name=\"query\"><br>");
			out.printf("\t<button name = \"Exact\" type=\"submit\">Search</button>%n");
			out.printf("</form>%n");

			out.printf("</body>%n");
			out.printf("</html>%n");
		} else {
			response.sendRedirect("/login");
		}
	}
}
