package com.pucp;

import edu.stanford.nlp.sentiment.BuildBinarizedDataset;
import edu.stanford.nlp.sentiment.SentimentTraining;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 11/29/2015.
 */
public class Helper {
    public void ConvertToTreeBank(String inputFile, String outputFile)
    {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(outputFile));
            System.setOut(out);
            String[] newargs = {"-input", inputFile};
            BuildBinarizedDataset.main(newargs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void TrainModel(String TrainFile, String DevFile, String ModelName)
    {
        String[] newargs = {"-numHid", "25", "-trainPath", TrainFile,"-devPath", DevFile ,"-model", ModelName , "-train" };
        SentimentTraining.main(newargs);
        //:java -cp "*" -mx8g edu.stanford.nlp.sentiment.SentimentTraining -numHid 25 -trainPath kaggler_stanford_train_processed_format_treeformat.tsv -devPath kaggler_dev_processed_format_treeformat.tsv -train -model kaggler_v2_model.ser.gz
    }
    public void ConvertToStanfordInput(String inputFile, String outputFile)
    {
        System.out.print("Processing. This will take about 30 mins or os...");
        // Input
        String dataFileName = inputFile;

        File dataFile = new File(dataFileName);

        try {
            FileReader fr = new FileReader(dataFile);
            BufferedReader bReader = new BufferedReader(fr);
            File fout = new File(outputFile);
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            HashMap allPhrases = new HashMap();
            HashMap localPhrases = new HashMap();
            String line;
            // skip the first line.
            bReader.readLine();

            Integer currentSentenceId = 0;
            int countP = 0;
            boolean skip = false;
            String sentence = "";
            while ((line = bReader.readLine()) != null) {
                // Splitting the content of tabbed separated line
                // PhraseId	SentenceId	Phrase	Sentiment
                String datavalue[] = line.split("\t");
                String phraseId = datavalue[0];
                Integer sentenceId = Integer.parseInt(datavalue[1]);
                String phrase = datavalue[2];
                Integer sentiment = Integer.parseInt(datavalue[3]);
                if(!currentSentenceId.equals(sentenceId)){
                    // new sentence starting.
                    Iterator it = allPhrases.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();

                        String key = pairs.getKey().toString().toLowerCase();
                        String[] words = sentence.split(" ");

                        HashMap doneWords = new HashMap();
                        if(key.split(" ").length == 1) {

                            for (String w : words) {

                                String wor = w.toLowerCase();
                                if (wor.equals(key)) {
                                    if (!doneWords.containsKey(w)) {
                                        doneWords.put(w, null);
                                        if (!localPhrases.containsKey(key)) {
                                            bw.write(pairs.getValue() + " " + w);
                                            bw.newLine();
                                        }
                                    }

                                }
                            }
                        }
                        else if(key.split(" ").length > 1){
                            if(sentence.contains(key)){
                                if(!localPhrases.containsKey(key)){
                                    if(pairs.getKey()==" "){
                                        System.out.println();
                                    }

                                    bw.write(pairs.getValue() + " " + pairs.getKey());
                                    bw.newLine();
                                }
                            }
                        }

                    }

                    sentence = phrase;
                    localPhrases = new HashMap();

                    if(skip==false){
                        bw.newLine();
                    }
                }


                currentSentenceId = sentenceId;


                if(skip==false) {

                    allPhrases.put(phrase,sentiment);
                    if (!phrase.equals(" ")) {


                        localPhrases.put(phrase, sentiment);
                        bw.write(sentiment + " " + phrase);
                        bw.newLine();
                        countP++;
                    }

                }

            }

            bReader.close();
            bw.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
