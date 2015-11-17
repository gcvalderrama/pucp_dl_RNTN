package com.pucp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 11/16/2015.
 */
public class KagglerSentimentAnalyzer {
    private String model;
    private StanfordCoreNLP pipeline;
    public KagglerSentimentAnalyzer(String model)
    {
        this.model = model;
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        if ( model != null)
        {
            props.setProperty("sentiment.model", this.model);
        }
        this.pipeline = new StanfordCoreNLP(props);
    }

    public int findSentiment(String line) {
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }
        return mainSentiment;
        /*
        if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
            return null;
        }
        TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line, toCss(mainSentiment));
        return tweetWithSentiment;
        */

    }

    public void Evaluate(String input, String output) throws  IOException
    {
        List<String> lines = new ArrayList<String>();
        File dataFile = new File(input);
        FileReader fr = new FileReader(dataFile);
        BufferedReader bReader = new BufferedReader(fr);

        File fout = new File(output);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        String line;
        bReader.readLine();

        while ((line = bReader.readLine()) != null) {

            String datavalue[] = line.split("\t");
            String phraseId = datavalue[0];
            Integer sentenceId = Integer.parseInt(datavalue[1]);
            String phrase = datavalue[2];
            int sentiment = this.findSentiment(phrase);
            String str = phraseId + "," + sentiment;
            System.out.println(str);
        }
        bReader.close();
        bw.close();

    }
}
