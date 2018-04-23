// Do not submit with package statements if you are using eclipse.
// Only use what is provided in the standard libraries.

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.*;

public class NaiveBayes {
    // store how many spams and hams emails
    // we have processed
    // for smoothing, we initialize them as 2
    private int NUM_HAM = 2;
    private int NUM_SPAM = 2;

    //P(S) and P(H)
    private double PS = 0.0;
    private double PH = 0.0;

    // store info about each word's number of appearance
    // in the spams and hams
    private Map<String, Integer> spam = new HashMap<String, Integer>();
    private Map<String, Integer> ham = new HashMap<String, Integer>();

    // for storing info about P(w|S) and P(w|H)
    private Map<String, Double> PrS = new HashMap<String, Double>();
    private Map<String, Double> PrH = new HashMap<String, Double>();

    /*
     * !! DO NOT CHANGE METHOD HEADER !!
     * If you change the method header here, our grading script won't
     * work and you will lose points!
     * 
     * Train your Naive Bayes Classifier based on the given training
     * ham and spam emails.
     *
     * Params:
     *      hams - email files labeled as 'ham'
     *      spams - email files labeled as 'spam'
     */
    public void train(File[] hams, File[] spams) throws IOException {
        // update NUMS
        this.NUM_HAM += hams.length;
        this.NUM_SPAM += spams.length;

        PS = NUM_SPAM * 1.0 / (NUM_SPAM + NUM_HAM * 1.0);
        PH = NUM_SPAM * 1.0 / (NUM_SPAM + NUM_HAM * 1.0);

        // processing hams
        for (int i = 0; i < hams.length; i++) {
            HashSet<String> tokens = tokenSet(hams[i]);
            for (String token : tokens) {
                if (!ham.containsKey(token)) {
                    ham.put(token, 0);
                }
                if (!spam.containsKey(token)) {
                    spam.put(token, 1);
                }
                int value = ham.get(token);
                value++;
                ham.put(token, value);
            }
        }

        // processing spams
        for (int i = 0; i < spams.length; i ++) {
            HashSet<String> tokens = tokenSet(spams[i]);
            for (String token : tokens) {
                if (!spam.containsKey(token)) {
                    spam.put(token, 0)
                }
                if (!ham.containsKey(token)) {
                    ham.put(token, 1);
                }
                int value = spams.get(token);
                value++;
                spam.put(token, value);
            }
        }
        // processing Probability
        for (String token : ham.keySet()) {
            int value = ham.get(token);
            // for smoothing
            value++;
            PrH.put(token, value*1.0/NUM_HAM);
        }

        // processing probability
        for (String token : spam.keySet()) {
            int value = spam.get(token);
            // for smoothing
            value++;
            PrS.put(token, value*1.0/NUM_SPAM);
        }
    }

    /*
     * !! DO NOT CHANGE METHOD HEADER !!
     * If you change the method header here, our grading script won't
     * work and you will lose points!
     *
     * Classify the given unlabeled set of emails. Follow the format in
     * example_output.txt and output your result to stdout. Note the order
     * of the emails in the output does NOT matter.
     * 
     * Do NOT directly process the file paths, to get the names of the
     * email files, check out File's getName() function.
     *
     * Params:
     *      emails - unlabeled email files to be classified
     */
    public void classify(File[] emails) throws IOException {
        for (int i = 0; i < emails.length; i ++) {
            double SS = Math.log(PS);
            double HH = Math.log(PH);
            HashSet<String> tokens = tokenSet(emails[i]);
            for (String token : tokens) {
                // check if the token is seen in our data
                if (this.ham.containsKey(token)) {
                    SS += Math.log(PrS.get(token));
                    HH += Math.log(PrH.get(token));
                }
            }
            double result = SS / (SS + HH);
            String tag = "ham";
            if (result > 0.5) {
                tag = "spam";
            }
            System.out.println((i + 1) + ".txt " + "tag");
        }
    }


    /*
     *  Helper Function:
     *  This function reads in a file and returns a set of all the tokens. 
     *  It ignores "Subject:" in the subject line.
     *  
     *  If the email had the following content:
     *  
     *  Subject: Get rid of your student loans
     *  Hi there ,
     *  If you work for us , we will give you money
     *  to repay your student loans . You will be 
     *  debt free !
     *  FakePerson_22393
     *  
     *  This function would return to you
     *  ['be', 'student', 'for', 'your', 'rid', 'we', 'of', 'free', 'you', 
     *   'us', 'Hi', 'give', '!', 'repay', 'will', 'loans', 'work', 
     *   'FakePerson_22393', ',', '.', 'money', 'Get', 'there', 'to', 'If', 
     *   'debt', 'You']
     */
    public static HashSet<String> tokenSet(File filename) throws IOException {
        HashSet<String> tokens = new HashSet<String>();
        Scanner filescan = new Scanner(filename);
        filescan.next(); // Ignoring "Subject"
        while(filescan.hasNextLine() && filescan.hasNext()) {
            tokens.add(filescan.next());
        }
        filescan.close();
        return tokens;
    }
}
