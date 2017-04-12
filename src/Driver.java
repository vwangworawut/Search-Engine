import java.nio.file.Paths;

public class Driver {

	public static void main(String[] args) {
		String output = "index.json";
		ArgumentParser argumentMap = new ArgumentParser(args);
		String queryOutput = "results.json";
		String input = null;

		try {
			WorkQueue workers = null;
			InvertedIndex index;
			InvertedIndexBuilder builder;
			QueryHelper query;
			WebCrawler webCrawler;

			if (argumentMap.hasFlag("-multi")) {
				int threads;
				if (argumentMap.hasValue("-multi") && 
						Integer.parseInt(argumentMap.getValue("-multi")) > 0) {
					threads = Integer.parseInt(argumentMap.getValue("-multi"));
				} else {
					threads = 5;
				}
				workers = new WorkQueue(threads);
				
				ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
				index = threadSafeIndex;
				builder = new MultiIndexBuilder(threadSafeIndex, workers);
				query = new MultiQueryHelper(threadSafeIndex, workers);
				webCrawler = new MultiWebCrawler(threadSafeIndex, workers);
			} else {
				index = new InvertedIndex();
				builder = new InvertedIndexBuilder(index);
				query = new QueryHelper(index);
				webCrawler = new WebCrawler(index);
			}
			if (argumentMap.hasFlag("-dir")) {
				if (argumentMap.getValue("-dir") != null) {
					input = argumentMap.getValue("-dir");
					builder.buildIndex(Paths.get(input));
				}
			}
			if (argumentMap.hasFlag("-url")) {
				webCrawler.startCrawl(argumentMap.getValue("-url"), index);
			}

			if (argumentMap.hasFlag("-index")) {
				if (argumentMap.getValue("-index") != null) {
					output = argumentMap.getValue("-index");
				}
				index.toJSON(Paths.get(output));
			}

			if (argumentMap.hasFlag("-query") && argumentMap.hasValue("-query")) {
				query.parseQuery(Paths.get(argumentMap.getValue("-query")), false);
			}

			if (argumentMap.hasFlag("-exact") && argumentMap.hasValue("-exact")) {
				query.parseQuery(Paths.get(argumentMap.getValue("-exact")), true);
			}

			if (argumentMap.hasFlag("-results")) {
				if (!argumentMap.hasValue("-results")) {
					query.toJSON(Paths.get(queryOutput));
				} else {
					query.toJSON(Paths.get(argumentMap.getValue("-results")));
				}
			}
			
			if (argumentMap.hasFlag("-port")) {
			String port = "8080";
			if (argumentMap.hasValue("-port")) {
				port = argumentMap.getValue("-port");
			}
			ContextServer server = new ContextServer(Integer.parseInt(port), index, webCrawler);
			server.startServers();
		}
			
			if (workers != null) {
				workers.shutdown();
			}
		} catch (NumberFormatException e) {
			System.out.println("Not valid thread value");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}