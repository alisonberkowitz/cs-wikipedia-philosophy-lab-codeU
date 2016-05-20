package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	static List visited = new ArrayList<String>();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		followLink(url);

        // the following throws an exception so the test fails
        // until you update the code
        // String msg = "Complete this lab by adding your code and removing this statement.";
        // throw new UnsupportedOperationException(msg);
	}

    private static void followLink(String url) throws IOException {
    	visited.add(url);
    	Elements paragraphs = wf.fetchWikipedia(url);

		String next = getFirstLink(paragraphs);
		if (next.equals("https://en.wikipedia.org/wiki/Philosophy")) {
			System.out.println("Philosophy Reached");
		} else if (visited.contains(next)) {
			String msg = "Link already visited";
			throw new UnsupportedOperationException(msg);
		} else {
			followLink(next);
		}
    }

    private static String getFirstLink(Elements paragraphs) {
		int i = 0;
		while (i<paragraphs.size()) {
			Element firstPara = paragraphs.get(i);
			Iterable<Node> iter = new WikiNodeIterable(firstPara);
			for (Node node: iter) {
				if (node instanceof Element) {
					Elements links = ((Element)node).select("a[href]");
					for (Element element: links) {
						if (!italicized(element) && !parenthesized((Element)node, element)) {
							String href = element.attr("href");
							// check for parentheses
							// link is not external, but not to the same page
							if (href.startsWith("/wiki")) {
								String absHref = element.attr("abs:href");
								System.out.println(absHref);
								return absHref;
							}
						}
					}
				}
			}
			i++;
		}
		String msg = "No links found";
		throw new UnsupportedOperationException(msg); 
    }

    private static boolean italicized(Element element) {
    	Elements parents = element.parents();
		boolean italics = false;
		for (Element parent: parents) {
			if (parent.tagName() == "i" || parent.tagName() == "em") {
				italics = true;
			}
		}
		return italics;
    }

    // checks if link is parenthesized in element
    private static boolean parenthesized(Element element, Element link) {
    	// if 0, parentheses are closed
		int parentheses = 0;
		String text = element.text();
		int linkIndex = text.indexOf(link.text());
		for (int i=0; i<linkIndex; i++) {
			if (text.charAt(i)=='(') {
				parentheses++;
			} else if (text.charAt(i)==')') {
				parentheses--;
			}
		}
		return parentheses > 0;
    }
}
