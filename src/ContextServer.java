import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ContextServer {

	private static int PORT;
	private final InvertedIndex index;
	private WebCrawler webCrawler;
	
	public ContextServer(int port, InvertedIndex index, WebCrawler webCrawler) {
		ContextServer.PORT = port;
		this.index = index;
		this.webCrawler = webCrawler;
	}

	public void startServers() throws Exception {

		Server server = new Server(PORT);

		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);

		handler.addServletWithMapping(LoginUserServlet.class,     "/login");
		handler.addServletWithMapping(LoginRegisterServlet.class, "/register");
		handler.addServletWithMapping(LoginWelcomeServlet.class,  "/welcome");
		handler.addServletWithMapping(LoginRedirectServlet.class, "/*");
		handler.addServletWithMapping(PartialSearchServlet.class, "/partialsearch");
		handler.addServletWithMapping(ExactSearchServlet.class, "/exactsearch");
		handler.addServletWithMapping(new ServletHolder(new SearchResultsServlet(index)), "/searchresults");
		handler.addServletWithMapping(SearchHistoryServlet.class, "/searchhistory");
		handler.addServletWithMapping(new ServletHolder(new NewCrawlServlet(webCrawler)), "/newcrawl");
		

		try {
			server.start();
			server.join();
		}
		catch (Exception ex) {
			System.exit(-1);
		}
	}
}
