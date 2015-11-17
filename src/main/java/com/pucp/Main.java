package com.pucp;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	// write your code here
        KagglerController controller = new KagglerController();

        controller.MakeMigration("UseComplexTrainDevStanford");

        controller.MigrateToTree_Train();

     /*   try{
//            KagglerSentimentAnalyzer analyzer = new KagglerSentimentAnalyzer(null);

  //          analyzer.Evaluate("kaggler_test.tsv",  "kaggler_result.tsv");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
*/
        System.out.println("======================== task finish ===================");


    }
}
