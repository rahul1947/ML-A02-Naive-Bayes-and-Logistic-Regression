package a02; // comment to execute

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/**
 * CS6375: Machine Learning 
 * Assignment 02: Naive Bayes and Logistic regression
 * 
 * @author Rahul Nalawade
 * Mar 01, 2018
 */

// Logistic Regression Implementation
public class LogisticRegression {
	static HashMap<String, Double> wtMatrix = new HashMap<String, Double>();
	static double lr;
	static double ld;
	static int itr;
	static double w0 = 0.1;
	
	//number of files in testing data 
	static int n=0;
	static int n1=0;
	
	public static Set<String> tokens = new HashSet<String>();
	public static Set<String> Stopword_list = new HashSet<String>();
	public static HashMap<String, Integer> Stokens = new HashMap<String, Integer>();
	public static HashMap<String, Integer> Htokens = new HashMap<String, Integer>();

	public static HashMap<String,HashMap<String, Integer> >Stokens1 = new HashMap<String, HashMap<String,Integer>>();	
	public static HashMap<String,HashMap<String, Integer> >Htokens1 = new HashMap<String, HashMap<String,Integer>>();

	private static void readFile(File mail) throws Exception {

		for(File m: mail.listFiles()) {

			Scanner scanner = new Scanner(m);
			while(scanner.hasNext()) {
				String line = scanner.nextLine();

				for(String s : line.toLowerCase().trim().split(" ")) {
					s = s.replaceAll("\\<.*?>","");	//replacing SGML tags with null
					s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]",""); //replacing special characters with null
					s = s.replaceAll("'s","");  //replacing 's with null
					s = s.replaceAll("\\'","");	//replacing ' with space
					s = s.replaceAll("-","");	//replacing - with space, making it two separate words
					s = s.replaceAll("\\.","");	 //replacing . with null eg: U.S --> US
					s = s.replaceAll("[0-9]+",""); //replacing digits with null
					if(!s.isEmpty()) {
						tokens.add(s);
					}
				}
			}
			scanner.close();
		}
	}

	static Set<String> hm = new HashSet<String>();
	static Set<String> sm = new HashSet<String>();
	static Set<String> am = new HashSet<String>();
	
	private static int tokenfrequency(String doc, String t) {
		int freq = 0;
		if(sm.contains(doc)) {
			try {
				for(Entry<String, Integer> fs: Stokens1.get(doc).entrySet()) {
					if(fs.getKey().equals(t)) {
						freq = fs.getValue();
						return freq;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		else if(hm.contains(doc)) {
			try {
				for(Entry<String, Integer> fh: Htokens1.get(doc).entrySet()) {
					if(fh.getKey().equals(t)) {
						freq = fh.getValue();
						return freq;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return 0;
	}

	private static double fileNet(String doc) {

		if(sm.contains(doc)) {
			double net1 = w0;
			try {
				for(Entry<String, Integer> fn: Stokens1.get(doc).entrySet()) {
					net1 += (fn.getValue()* wtMatrix.get( fn.getKey() ));
				}	
			} catch(Exception e) {
				e.printStackTrace();
			}

			return (ApplyLogistic(net1));
		}
		else {
			double net = w0;
			try {
				for(Entry<String, Integer> fh: Htokens1.get(doc).entrySet()) {
					net += (fh.getValue()* wtMatrix.get( fh.getKey() ));
				}	
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return (ApplyLogistic(net));
		}
	}

	private static double ApplyLogistic(double net) {
	//As exponent of high values overflows, we dont take high values
		if(net<-100) {
			return 0.0;
		}
		else if(net>100) {
			return 1.0;
		}
		else {
			return (1.0 /(1.0+ Math.exp(-net))); //Sigmoid function
		}
	}

	public static double readTest(File mails,int d,String removeSW) throws Exception {
		if(d==1) { //spam
			int c1 = 0 ;
			for(File testfile : mails.listFiles()) {
				n1 = n1+1;
				HashMap<String, Integer> hmtest = new HashMap<String, Integer>();
				Scanner sc = new Scanner(testfile);
				
				while(sc.hasNext()) {
					String line = sc.nextLine();
					for(String s: line.toLowerCase().trim().split(" ")) {
						s = s.replaceAll("\\<.*?>","");	//replacing SGML tags with null
						s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]","");	//replacing special characters with null
						s = s.replaceAll("'s","");  //replacing 's with null
						s = s.replaceAll("\\'","");	//replacing ' with space
						s = s.replaceAll("-","");	//replacing - with space, making it two separate words
						s = s.replaceAll("\\.","");	 //replacing . with null eg: U.S --> US
						s = s.replaceAll("[0-9]+",""); //replacing digits with null
						if(hmtest.containsKey(s))
							hmtest.put(s, hmtest.get(s)+1);
						else
							hmtest.put(s, 1);
					}	
				}

				sc.close();
			
				if(removeSW.equals("yes")) {
					for(String stopword: Stopword_list) {
						if(hmtest.containsKey(stopword)) {
							hmtest.remove(stopword);
						}
					}
				}
				
				int res = test(hmtest);
				if(res == 1)
					c1++;
			}
	
			return c1;
		}
		else {
			int c = 0 ;
			n = mails.listFiles().length;

			for(File testfile : mails.listFiles()) {
				HashMap<String, Integer> hmtest = new HashMap<String, Integer>();
				Scanner sc = new Scanner(testfile);

				while(sc.hasNext()) {
					
					String line = sc.nextLine();
					for(String s: line.toLowerCase().trim().split(" ")) {
						s = s.replaceAll("\\<.*?>","");	//replacing SGML tags with null
						s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]","");	//replacing special characters with null
						s = s.replaceAll("'s","");  //replacing 's with null
						s = s.replaceAll("\\'","");	//replacing ' with space
						s = s.replaceAll("-","");	//replacing - with space, making it two separate words
						s = s.replaceAll("\\.","");	 //replacing . with null eg: U.S --> US
						s = s.replaceAll("[0-9]+",""); //replacing digits with null

						if(hmtest.containsKey(s)) {
							hmtest.put(s, hmtest.get(s)+1);
						}
						else {
							hmtest.put(s, 1);
						}
					}
				}

				sc.close();
				int re = test(hmtest);
				if(re == 0) {
					c++;
				}
			}
			
			return c;
		}
	}
	
	public static int test(HashMap<String, Integer> hmtest) {
		double result = 0;
		
		for(Entry<String, Integer> m :hmtest.entrySet()) {
			if(wtMatrix.containsKey(m.getKey())) {
				result += (m.getValue()* wtMatrix.get(m.getKey()));
			}
		}
		result+=w0;
		
		if(result<0)
			return 0;
		else
			return 1;
	}
		
	private static void fileTokenDirectory(File mails, int d) throws Exception {
		if(d==1) { //spam
			for(File ml1: mails.listFiles()) {
				HashMap<String, Integer> inners = new HashMap<String, Integer>();

				sm.add(ml1.getName());
				am.add(ml1.getName());
				Scanner sc = new Scanner(ml1);
				while(sc.hasNext()){
					String line = sc.nextLine();

					for(String s: line.toLowerCase().trim().split(" ")) {
						s = s.replaceAll("\\<.*?>","");	//replacing SGML tags with null
						s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]","");	//replacing special characters with null
						s = s.replaceAll("'s","");  //replacing 's with null
						s = s.replaceAll("\\'","");	//replacing ' with space
						s = s.replaceAll("-","");	//replacing - with space, making it two separate words
						s = s.replaceAll("\\.","");	 //replacing . with null eg: U.S --> US
						s = s.replaceAll("[0-9]+",""); //replacing digits with null
						
						if(tokens.contains(s)) {

							if(Stokens.containsKey(s)) {
								Stokens.put(s, Stokens.get(s)+1);
							}
							else {
								Stokens.put(s, 1);
							}

							if(inners.containsKey(s)) {
								inners.put(s, inners.get(s)+1);
							}
							else {
								inners.put(s, 1);
							}
						}

						Stokens1.put(ml1.getName(), inners);
					}
				}
				sc.close();
			}
		}
		else { //ham
			for(File ml: mails.listFiles()) {
				HashMap<String, Integer> innerh = new HashMap<String, Integer>();
				hm.add(ml.getName());
				am.add(ml.getName());

				Scanner sc = new Scanner(ml);
				while(sc.hasNext()) {
					String line = sc.nextLine();
					for(String s: line.toLowerCase().trim().split(" ")) {
						s = s.replaceAll("\\<.*?>","");	//replacing SGML tags with null
						//s = s.replaceAll("[0-9]+",""); //replacing digits with null
						s = s.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]",""); //replacing special characters with null
						s = s.replaceAll("'s",""); //replacing 's with null
						s = s.replaceAll("\\'",""); //replacing ' with space
						s = s.replaceAll("-",""); //replacing - with space, making it two separate words
						s = s.replaceAll("\\.",""); //replacing . with null eg: U.S --> US
						s = s.replaceAll("[0-9]+",""); //replacing digits with null
						
						if(!s.isEmpty()) {

							if(tokens.contains(s)) {
								if(Htokens.containsKey(s))
									Htokens.put(s, Htokens.get(s)+1);
								else
									Htokens.put(s, 1);
							}
						}

						if(!s.isEmpty()) {

							if(tokens.contains(s)) {
								if(innerh.containsKey(s)) {
									innerh.put(s, innerh.get(s)+1);
								}
								else {
									innerh.put(s, 1);
								}
							}
						}

						Htokens1.put(ml.getName(), innerh);
					}
				}
				sc.close();
			}
		}
	}

	/*Assign random weights to all tokens*/	
	public static void trainData() {
		
		for(String t:tokens) {
			double w =  2 * Math.random() - 1;
			wtMatrix.put(t, w);
		}
		
		for(String t : tokens) {
			double DE = 0;

			for(String doc : am) {
				double target;
				int freq = tokenfrequency(doc, t);
				
				if(sm.contains(doc)) {
					target = 1;//spam
				}
				else {
					target = 0;//ham
				}
				
				double output = fileNet(doc);
				double E = (target - output);
				DE = DE + freq*E;
			}
			
			double wtNew = wtMatrix.get(t) + lr*(DE -(ld*wtMatrix.get(t)));
			wtMatrix.put(t, wtNew);
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 6) {
			System.out.println("Not enough command line arguments Existing..");
			return;
		}

		String train = args[0];
		File hwtrainS = new File(train+"/spam");
		File hwtrainH = new File(train+"/ham");
		String test = args[1];
		File hwtestS = new File(test+"/spam");
		File hwtestH = new File(test+"/ham");
		
		String removeSW = args[2];
		lr = Double.parseDouble(args[3]);
		ld = Double.parseDouble(args[4]);
		itr = Integer.parseInt(args[5]);

		//Extract tokens from training data
		readFile(hwtrainS);
		readFile(hwtrainH);

		/*Removing stop words*/
		if(removeSW.equals("yes")) {

			System.out.println("After removing Stop Words");
			File stopwords = new File("stopwords.txt");
			Scanner st =null;
			try {
				st = new Scanner(stopwords);
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}

			while(st.hasNext()) {
				String sw = st.next();
				Stopword_list.add(sw);
			}

			st.close();
			for(String sw : Stopword_list) {
				if(tokens.contains(sw))				
					tokens.remove(sw);
			}
		}

		/*record corresponding token, its frequency of each file*/
		fileTokenDirectory(hwtrainS,1);
		fileTokenDirectory(hwtrainH,0);

		trainData();
		/*Read test data*/
		double sc = readTest(hwtestS,1,removeSW);
		double hc = readTest(hwtestH,0,removeSW);
		System.out.println("Accuracy on Test data: "+((sc+hc)/(n+n1))*100);	
	}
}