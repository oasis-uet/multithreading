import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerNonThreaded {

    private Map<String, Integer> visitedUrls;
    private Deque<String> urlsQueue;
    private Instant startTime;
    private final int maxDepth = 4;
    private final int maxUrlsPerPage = 10;

    public WebCrawlerNonThreaded(Instant start) {
        visitedUrls = new HashMap<>();
        urlsQueue = new LinkedList<>();
        startTime = start;
    }

    public void crawl(String rootUrl) {
        urlsQueue.addLast(rootUrl);
        visitedUrls.put(rootUrl, 1);

        while (!urlsQueue.isEmpty()) {
            String url = urlsQueue.removeFirst();
            int depth = visitedUrls.get(url);

            if (depth < maxDepth) {
                try {
                    URL urlObject = new URL(url);
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlObject.openStream()));
                    String inputLine = in.readLine();
                    String rawHtml = "";

                    while(inputLine != null){
                        rawHtml += inputLine;
                        inputLine = in.readLine();
                    }

                    in.close();
                    parseAndAddUrls(rawHtml, depth);
                } catch (IOException e) {}
            }
        }
    }

    private void parseAndAddUrls(String rawHtml, int depth) {
        String urlPattern = "((\\/wiki\\/)+[^\\s\\.\\#\\:\"]+[\\w])\"";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(rawHtml);

        int cntUrlsPerPage = 0;

        while (matcher.find()) {
            String newUrl = matcher.group(1);
            newUrl = "https://en.wikipedia.org" + newUrl;

            if (!visitedUrls.containsKey(newUrl)) {
                urlsQueue.addLast(newUrl);
                visitedUrls.put(newUrl, depth+1);
                cntUrlsPerPage += 1;

                if (cntUrlsPerPage >= maxUrlsPerPage) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        Instant start = Instant.now();
        WebCrawlerNonThreaded crawler = new WebCrawlerNonThreaded(start);
        crawler.crawl("https://en.wikipedia.org/wiki/Travelling_salesman_problem");


        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Visited " + crawler.visitedUrls.size() +" Urls  in " + totalTime/1000000 +" ms");

    }
}