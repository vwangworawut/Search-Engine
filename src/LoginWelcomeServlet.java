import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles display of user information.
 *
 * @see LoginServer
 */
@SuppressWarnings("serial")
public class LoginWelcomeServlet extends LoginBaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String user = getUsername(request);

		if (user != null) {
			prepareResponse("Welcome to USF Search", response);

			PrintWriter out = response.getWriter();
			
			out.printf("<div style=\"background:url(https://pbs.twimg.com/profile_images/1503726907/twitterlogo-usf-hex.png) white bottom right no-repeat scroll;border:0px solid black;width:900px;height:500px;font-size:18px;\">");
			out.println("<p>Hello " + user + "!</p>");
			

			out.println("<p><a href=\"/partialsearch\" class=\"btn btn-primary\" role=\"button\">Partial Search</a></p>");
			out.println("<p><a href=\"/exactsearch\" class=\"btn btn-primary\" role=\"button\">Exact Search</a></p>");
			out.println("<p><a href=\"/newcrawl\" class=\"btn btn-primary\" role=\"button\">New Crawl</a></p>");
			out.println("<p><a href=\"/searchhistory\" class=\"btn btn-primary\" role=\"button\">Search History</a></p>");
			out.println("<p><a href=\"/login?logout\" class=\"btn btn-primary\" role=\"button\">Logout</a></p>");

			
			//finishResponse(response);
		}
		else {
			response.sendRedirect("/login");
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
