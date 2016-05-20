package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayDeque;
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
		if (next == "https://en.wikipedia.org/wiki/Philosophy") {
			//indicate success and exit ?
		} else if (visited.contains(next)) {
			String msg = "Link already visited";
			throw new UnsupportedOperationException(msg);
		} else {
			followLink(next);
		}
    }

    private static String getFirstLink(Elements paragraphs) {
    	// if empty, parentheses are closed
		ArrayDeque<String> parentheses = new ArrayDeque<String>();
		int i = 0;
		while (i<paragraphs.size()) {
			Element firstPara = paragraphs.get(i);
			Iterable<Node> iter = new WikiNodeIterable(firstPara);
			for (Node node: iter) {
				if (node instanceof TextNode) {
					System.out.println("TextNode");
					for (char ch: ((TextNode)node).text().toCharArray()) {
						if (ch == '(') {
							System.out.println("(");
							parentheses.push("(");
						} else if (ch == ')') {
							System.out.println(")");
							parentheses.pop();
						}
					}
				}
				if (node instanceof Element) {
					Elements links = ((Element)node).select("a[href]");
					for (Element element: links) {
						if (!italicized(element) && parentheses.isEmpty()) {
							String href = element.attr("href");
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
}
