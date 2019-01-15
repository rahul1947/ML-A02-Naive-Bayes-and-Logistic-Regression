package a02; // comment to execute

import java.util.Scanner;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.io.File;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.util.TreeMap;

/**
 * CS6375: Machine Learning 
 * Assignment 02: Naive Bayes and Logistic regression
 * 
 * @author Rahul Nalawade
 * Mar 01, 2018
 */

// Naive Bayes Implementation
public class NaiveBayes {

	static double PriorS = 0.0; // prior probability of spam
	static double PriorH = 0.0; // prior probability of ham
	static int stotal = 0;
	static int htotal = 0;

	public static Set<String> Stopword_list = new HashSet<String>(); // to store list of stop words
	public static Set<String> tokens = new HashSet<String>(); // to store unique tokens in the training set
	public static TreeMap<String, Integer> hmS = new TreeMap<String, Integer>(); // storing spam tokens
	public static TreeMap<String, Integer> hmH = new TreeMap<String, Integer>(); // storing ham tokens

	// For storing conditional probabilities
	static HashMap<String, Double> condprobs = new HashMap<String, Double>();
	static HashMap<String, Double> condprobh = new HashMap<String, Double>();

	private static double calculateLog(double fraction) {
		return Math.log10(fraction) / Math.log10(2);
	}

	/* To store token & its corresponding frequencies */
	private static void filetokenDirectory(File mails, int d) throws Exception {
		if (d == 1) { // spam
			for (File me : mails.listFiles()) {
				Scanner sc = new Scanner(me);

				while (sc.hasNext()) {
					String line = sc.nextLine();
					for (String s : line.toLowerCase().trim().split(" ")) {
						s = s.replaceAll("\\<.*?>", ""); // replacing SGML tags with null
						s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", ""); // replacing special characters with null
						s = s.replaceAll("'s", ""); // replacing 's with null
						s = s.replaceAll("\\'", ""); // replacing '
						s = s.replaceAll("-", ""); // replacing -, making it two separate words
						s = s.replaceAll("\\.", ""); // replacing . with null eg: U.S --> US
						s = s.replaceAll("[0-9]+", ""); // replacing digits with null

						if (!s.isEmpty()) {
							if (tokens.contains(s)) {
								if (hmS.containsKey(s))
									hmS.put(s, hmS.get(s) + 1);
								else
									hmS.put(s, 1);
							}
						}
					}
				}
				sc.close();
			}
		} else {// ham
			for (File ae : mails.listFiles()) {
				Scanner sc = new Scanner(ae);

				while (sc.hasNext()) {
					String line = sc.nextLine();
					for (String s : line.toLowerCase().trim().split(" ")) {
						s = s.replaceAll("\\<.*?>", ""); // replacing SGML tags with null
						s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", ""); // replacing special characters with null
						s = s.replaceAll("'s", ""); // replacing 's with null
						s = s.replaceAll("\\'", ""); // replacing '
						s = s.replaceAll("-", ""); // replacing -, making it two separate words
						s = s.replaceAll("\\.", ""); // replacing . with null eg: U.S --> US
						s = s.replaceAll("[0-9]+", ""); // replacing digits with null

						if (!s.isEmpty()) {
							if (tokens.contains(s)) {
								if (hmH.containsKey(s))
									hmH.put(s, hmH.get(s) + 1);
								else
									hmH.put(s, 1);
							}
						}
					}
				}
				sc.close();
			}
		}
	}

	private static void readFile(File mails) throws Exception {

		for (File file : mails.listFiles()) {

			Scanner scanner = new Scanner(file);

			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				for (String s : line.toLowerCase().trim().split(" ")) {
					s = s.replaceAll("\\<.*?>", ""); // replacing SGML tags with null
					s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", ""); // replacing special characters with null
					s = s.replaceAll("'s", ""); // replacing 's with null
					s = s.replaceAll("\\'", ""); // replacing '
					s = s.replaceAll("-", ""); // replacing -, making it two separate words
					s = s.replaceAll("\\.", ""); // replacing . with null eg: U.S --> US
					s = s.replaceAll("[0-9]+", ""); // replacing digits with null

					if (!s.isEmpty()) {
						tokens.add(s);
					}
				}
			}
			scanner.close();
		}
	}

	public static void countTokens(int d) {
		if (d == 1) {// spam
			for (Entry<String, Integer> entry : hmS.entrySet()) {
				stotal += entry.getValue();
			}
		} else {// ham
			for (Entry<String, Integer> entry : hmH.entrySet()) {
				htotal += entry.getValue();
			}
		}
	}

	public static void trainMNB(File hwTrainS, File hwTrainH) throws Exception {
		// Calculating Prior Probabilities//
		double S = 1.0 * (hwTrainS.listFiles().length) / (hwTrainS.listFiles().length + hwTrainH.listFiles().length);
		double H = 1.0 - PriorS;
		PriorS = Math.log(S);
		PriorH = Math.log(H);

		countTokens(1);
		countTokens(0);

		for (String s : tokens) {
			if (hmS.containsKey(s)) {
				double a = (hmS.get(s) + 1.0) / (stotal + tokens.size() + 1.0);
				double b = Math.log(a);
				condprobs.put(s, b);
			}
		}

		for (String s : tokens) {
			if (hmH.containsKey(s)) {
				double c = (hmH.get(s) + 1.0) / (htotal + tokens.size() + 1.0);
				double d = Math.log(c);
				condprobh.put(s, d);
			}
		}
	}

	public static double readTest(File mails, String FilterStopWords, int d) throws Exception {
		// spam
		if (d == 1) {
			double correctS = 0;
			//int ns = 0;

			for (File file : mails.listFiles()) {
				if (applyMNB(file, PriorH, PriorS, Stopword_list, FilterStopWords) == 1) {
					//ns += 1;
					correctS += 1.0;
				}
			}
			return correctS;
		}
		// ham
		else {
			double correctH = 0;
			int nh = 0;

			for (File file : mails.listFiles()) {
				nh = nh + 1;
				if (applyMNB(file, PriorH, PriorS, Stopword_list, FilterStopWords) == 0) {
					correctH += 1.0;
				}
			}
			return correctH;
		}
	}

	public static int applyMNB(File file, double PriorH, double PriorS, Set<String> stopword_list, String tofilter)
			throws Exception {

		Scanner scanner = new Scanner(file);
		double sa = 0;
		double ha = 0;

		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (tofilter.equals("yes")) {
				for (String s : line.toLowerCase().split(" ")) {
					s = s.replaceAll("\\<.*?>", ""); // replacing SGML tags with null
					s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", ""); // replacing special characters with null
					s = s.replaceAll("'s", ""); // replacing 's with null
					s = s.replaceAll("\\'", ""); // replacing '
					s = s.replaceAll("-", ""); // replacing -, making it two separate words
					s = s.replaceAll("\\.", ""); // replacing . with null eg: U.S --> US
					s = s.replaceAll("[0-9]+", ""); // replacing digits with null

					if (!stopword_list.contains(s)) {
						if (condprobs.containsKey(s)) {
							sa += condprobs.get(s);
						} else {
							sa += Math.log(1.0 / (stotal + tokens.size() + 1.0));
						}
						if (condprobh.containsKey(s))
							ha += condprobh.get(s);
						else
							ha += Math.log(1.0 / (htotal + tokens.size() + 1.0));
					}
				}
			} else {
				for (String s : line.toLowerCase().split(" ")) {
					s = s.replaceAll("\\<.*?>", ""); // replacing SGML tags with null
					// s = s.replaceAll("[0-9]+",""); //replacing digits with null
					s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", ""); // replacing special characters with null
					s = s.replaceAll("'s", ""); // replacing 's with null
					s = s.replaceAll("\\'", ""); // replacing '
					s = s.replaceAll("-", ""); // replacing -, making it two separate words
					s = s.replaceAll("\\.", ""); // replacing . with null eg: U.S --> US
					s = s.replaceAll("[0-9]+", ""); // replacing digits with null

					if (condprobs.containsKey(s)) {
						sa += condprobs.get(s);
					} else {
						sa += Math.log(1.0 / (stotal + tokens.size() + 1.0));
					}
					if (condprobh.containsKey(s))
						ha += condprobh.get(s);
					else
						ha += Math.log(1.0 / (htotal + tokens.size() + 1.0));
				}
			}
		}

		scanner.close();
		sa = sa + PriorS;
		ha = ha + PriorH;

		if (sa > ha) {// spam
			return 1;
		} else {
			return 0;
		}
	}

	public static void main(String[] cm) throws Exception {

		if (cm.length != 3) {
			System.out.println("Not enough command line arguments Existing..");
			return;
		}

		// Accessing training and testing datasets
		String TrainFolder = cm[0];
		File hwTrainS = new File(TrainFolder + "/spam");
		File hwTrainH = new File(TrainFolder + "/ham");

		String TestFolder = cm[1];
		File hwTestS = new File(TestFolder + "/spam");
		File hwTestH = new File(TestFolder + "/ham");

		String FilterStopWords = cm[2];
		File stopwords = new File("stopwords.txt");

		readFile(hwTrainS);
		readFile(hwTrainH);

		if (FilterStopWords.equals("yes")) {
			System.out.println("After filtering Stop Words");
			Scanner s = null;
			try {
				s = new Scanner(stopwords);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			while (s.hasNext()) {
				String sw = s.next();
				Stopword_list.add(sw);
			}

			s.close();

			for (String str : Stopword_list) {
				if (tokens.contains(str)) {
					tokens.remove(str);
				}
			}
		}

		filetokenDirectory(hwTrainS, 1);
		filetokenDirectory(hwTrainH, 0);

		trainMNB(hwTrainS, hwTrainH);

		double correctS = readTest(hwTestS, FilterStopWords, 1);
		double correctH = readTest(hwTestH, FilterStopWords, 0);

		double accuracy = 0.0;
		double tot = hwTrainS.listFiles().length + hwTrainH.listFiles().length;
		accuracy = ((double) correctH + (double) correctS) / tot;
		System.out.println("Naive Bayes Accuracy: " + accuracy * 100);
	}
}