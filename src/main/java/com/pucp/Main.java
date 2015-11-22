package com.pucp;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	// write your code here
        KagglerController controller = new KagglerController();

        controller.EvaluateModel("kaggler-dev.ser-kaggler_stanford_77.69.gz", "kaggler_train.tsv", "kaggler_train_eval_k_s.tsv" );

        //controller.EvaluateModel(null, "kaggler_train.tsv", "kaggler_train_eval.tsv" );
      //  controller.MakeMigration("UseComplexTrainDevStanford");
       // controller.MigrateToTree_Train();
/*
        try{
           KagglerSentimentAnalyzer analyzer = new KagglerSentimentAnalyzer("kaggler-dev.ser-full.gz");
           analyzer.Evaluate("kaggler_test.tsv",  "kaggler_full_result.tsv");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
*/
        System.out.println("======================== task finish ===================");


    }
}
