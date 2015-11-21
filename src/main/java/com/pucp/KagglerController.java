package com.pucp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.BuildBinarizedDataset;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentTraining;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.util.*;


public class KagglerController {

    private String original_file = "kaggler_train.tsv";
    private String train_file = "base_train.tsv";
    private String binary_train_dataset__file = "binary_train_dataset__file.csv";
    private String binary_dev_dataset_file = "binary_dev_dataset_file.csv";
    private String dev_file = "base_dev.tsv";
    private String test_file = "test.tsv";
    private String result_file = "result.tsv";

    private StanfordCoreNLP train_pipeline;

    public KagglerController()
    {

    }

    class KagglerSentence {
        public String phraseId;
        public Integer sentenceId;
        public String phrase;
        public Integer sentiment;
        public Integer eval_sentiment = 0;

        public KagglerSentence(String line)
        {
            String datavalue[] = line.split("\t");
            this.phraseId = datavalue[0];
            this.sentenceId= Integer.parseInt(datavalue[1]);
            this.phrase = datavalue[2];
            this.sentiment = Integer.parseInt(datavalue[3]);
        }
        public String Toline()
        {
            return  this.sentenceId +"|"+  this.phraseId + "|" + this.phrase +"|" +this.sentiment + "|" + this.eval_sentiment ;
        }
    }
    private  int findSentiment(StanfordCoreNLP pipeline,  String line) {
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
    }

    public void EvaluateModel(String Model, String Input, String OutputFile)
    {

        StanfordCoreNLP pipeline;
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        if ( Model != null)
        {
            props.setProperty("sentiment.model", Model);
        }
        pipeline = new StanfordCoreNLP(props);
        // Input
        File dataFile = new File(Input);

        try {

            FileReader fr = new FileReader(dataFile);

            BufferedReader bReader = new BufferedReader(fr);

            File fout = new File(OutputFile);

            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            String line;
            // skip the first line.
            bReader.readLine();

            while ((line = bReader.readLine()) != null) {
                KagglerSentence ksentence = new KagglerSentence(line);

                ksentence.eval_sentiment = this.findSentiment(pipeline, ksentence.phrase);

                bw.write(ksentence.Toline());
                bw.newLine();
            }
            bReader.close();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void MakeMigration(String Option)
    {
        if (Option == "UseSimpleTrainDevStanford")
        {
            UseSimpleTrainDevStanford();
        }
        if (Option == "UseComplexTrainDevStanford")
        {
            UseComplexTrainDevStanford();
        }

    }
    private void UseComplexTrainDevStanford()
    {
        System.out.print("Processing. This will take about 30 mins or os...");
        // Input
        String dataFileName = this.original_file;

        File dataFile = new File(dataFileName);

        try {
            FileReader fr = new FileReader(dataFile);
            BufferedReader bReader = new BufferedReader(fr);
            File fout = new File(this.binary_train_dataset__file);
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
    private void UseSimpleTrainDevStanford()
    {
        // Input
        File dataFile = new File(this.original_file);

        try {

            FileReader fr = new FileReader(dataFile);

            BufferedReader bReader = new BufferedReader(fr);

            File fout = new File(this.binary_train_dataset__file);

            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            String line;
            // skip the first line.
            bReader.readLine();

            Integer currentSentenceId = 0;

            Integer train_index = 0;

            while ((line = bReader.readLine()) != null) {
                KagglerSentence ksentence = new KagglerSentence(line);

                if(!currentSentenceId.equals(ksentence.sentenceId) && train_index != 0)
                {
                    bw.newLine();
                }
                bw.write(ksentence.sentiment + " " + ksentence.phrase);
                bw.newLine();
                train_index +=1;

                currentSentenceId = ksentence.sentenceId;
            }
            bReader.close();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void Migrate()
    {
        // Input
        File dataFile = new File(this.original_file);

        try {

            FileReader fr = new FileReader(dataFile);

            BufferedReader bReader = new BufferedReader(fr);

            File fout = new File(this.binary_train_dataset__file);

            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            File foutdev = new File(this.binary_dev_dataset_file);

            FileOutputStream  fodevstream = new FileOutputStream(foutdev);

            BufferedWriter bwdev = new BufferedWriter(new OutputStreamWriter(fodevstream));

            String line;
            // skip the first line.
            bReader.readLine();

            Integer currentSentenceId = 0;

            Integer train_index = 0;

            Integer dev_index = 0;

            while ((line = bReader.readLine()) != null) {
                KagglerSentence ksentence = new KagglerSentence(line);

                //if it is not the same sentence id
                if (ksentence.sentenceId % 10 == 0)
                {
                    if(!currentSentenceId.equals(ksentence.sentenceId) && dev_index != 0)
                    {
                        bwdev.newLine();
                    }
                    bwdev.write(ksentence.sentiment + " " + ksentence.phrase);
                    bwdev.newLine();
                    dev_index +=1;
                }
                else
                {
                    if(!currentSentenceId.equals(ksentence.sentenceId) && train_index != 0)
                    {
                        bw.newLine();
                    }
                    bw.write(ksentence.sentiment + " " + ksentence.phrase);
                    bw.newLine();
                    train_index +=1;
                }
                currentSentenceId = ksentence.sentenceId;
            }
            bReader.close();
            bw.close();
            bwdev.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void MigrateToTree_Train()
    {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(this.train_file));
            System.setOut(out);
            String[] newargs = {"-input", this.binary_train_dataset__file};
            BuildBinarizedDataset.main(newargs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void MigrateToTree_Dev()
    {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(this.dev_file));
            System.setOut(out);
            String[] newargs = {"-input", this.binary_dev_dataset_file};
            BuildBinarizedDataset.main(newargs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void TrainModel()
    {
        String[] newargs = {"-numHid", "25", "-trainPath", this.train_file,"-model", "kaggler-model.ser.gz" , "-train" };
        SentimentTraining.main(newargs);
        //:java -cp "*" -mx8g edu.stanford.nlp.sentiment.SentimentTraining -numHid 25 -trainPath manual.txt -devPath dev.txt -train -model model-dev.ser.gz
    }
}
