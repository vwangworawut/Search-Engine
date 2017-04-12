import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class NewCrawlServlet extends LoginBaseServlet {

	private static final String TITLE = "New Crawl";
	private WebCrawler webCrawler;

	public NewCrawlServlet(WebCrawler webCrawler) {
		this.webCrawler = webCrawler;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = getUsername(request);

		if (user != null) {

			if (request.getParameter("crawl") != null) {
				webCrawler.crawl(request.getParameter("crawl"), webCrawler.getIndex());
			}
			
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.printf("<div style=\"background:url(https://pbs.twimg.com/profile_images/1503726907/twitterlogo-usf-hex.png) white bottom right no-repeat scroll;border:0px solid black;width:900px;height:500px;font-size:18px;\">");
			

			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");

			out.printf("<form action =\"newcrawl\">%n");
			out.printf("<input type=\"text\" name=\"crawl\"><br>");
			out.printf("\t<button name = \"NewCrawl\" type=\"submit\">New Crawl</button>%n");
			out.printf("</form>%n");
			
			out.printf("<form action =\"welcome\">%n");
			out.printf("\t<button name = \"welcome\" type=\"submit\">Main Page</button>%n");
			out.printf("</form>%n");
		}
	}
}
