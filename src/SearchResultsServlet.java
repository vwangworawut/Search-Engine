import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SearchResultsServlet extends LoginBaseServlet {

	private static final String TITLE = "searchresults";
	private final InvertedIndex index;

	public SearchResultsServlet(InvertedIndex index) {
		this.index = index;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = getUsername(request);
		long start = 0;
		long time = 0;
		long end = 0;
		ArrayList<QueryObject> result = new ArrayList<>();

		if (user != null) {
			String[] query;
			if (request.getParameter("query").contains("+")) {
				query = request.getParameter("query").split("+");
			} else {
				query = request.getParameterValues("query");
			}

			String search = request.getParameter("query");
			response.addCookie(new Cookie("query" + user + System.currentTimeMillis(),
					"Searched for: <b>" + search + "</b> at " + CookieBaseServlet.getLongDate()));

			if (request.getParameter("Partial") != null) {
				start = System.currentTimeMillis();
				result = index.partialSearch(query);
				end = System.currentTimeMillis();
				time = end - start;
			}

			if (request.getParameter("Exact") != null) {
				start = System.currentTimeMillis();
				result = index.exactSearch(query);
				end = System.currentTimeMillis();
				time = end - start;
			}

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.printf("<div style=\"background:url(https://pbs.twimg.com/profile_images/1503726907/twitterlogo-usf-hex.png) white bottom right no-repeat scroll;border:0px solid black;width:900px;height:500px;font-size:18px;\">");
			
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");

			out.printf("<h1 align=\"center\">Search Results</h1>%n%n");

			out.printf("It took " + time + " miliseconds for " + result.size() + " results");
			for (QueryObject o : result) {
				out.printf("<p><a href=" + o.getLocation() + ">" + o.getLocation() + "</a><p>");
			}
			out.printf("<form action =\"/welcome\">%n");
			out.printf("<input type=\"submit\" value=\"Main Page\">");
			out.printf("</form>%n");

			out.printf("<form action =\"/searchhistory\">%n");
			out.printf("<input type=\"submit\" value=\"Search History\">");
			out.printf("</form>%n");

			out.printf("</body>%n");
			out.printf("</html>%n");
		} else {
			response.sendRedirect("/login");
		}
	}
}
