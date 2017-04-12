import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SearchHistoryServlet extends LoginBaseServlet {

	private static final String TITLE = "Search History";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = getUsername(request);

		if (user != null) {

			if (request.getParameter("Clear") != null) {
				CookieBaseServlet.clearCookies(request, response, "query" + user);
			}

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.printf("<div style=\"background:url(https://pbs.twimg.com/profile_images/1503726907/twitterlogo-usf-hex.png) white bottom right no-repeat scroll;border:0px solid black;width:1000px;height:500px;font-size:18px;\">");
			
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");

			out.printf("<h1 align=\"center\">Search History</h1>%n%n");

			Cookie[] cookie = request.getCookies();

			if (request.getParameter("Clear") == null) {
				for (int i = cookie.length - 1; i >= 0; i--) {
					if (cookie[i].getName().startsWith("query" + user)) {
						out.printf("<p>" + cookie[i].getValue() + "<p>");
					}
				}
			}

			out.printf("<form action =\"/\">%n");
			out.printf("<input type=\"submit\" value=\"Main Page\">");
			out.printf("</form>%n");

			out.printf("<form action =\"/searchhistory\">%n");
			out.printf("\t<button name = \"Clear\" type=\"submit\">Clear History</button>%n");
			out.printf("</form>%n");

			out.printf("</body>%n");
			out.printf("</html>%n");
		} else {
			response.sendRedirect("/login");
		}
	}
}
