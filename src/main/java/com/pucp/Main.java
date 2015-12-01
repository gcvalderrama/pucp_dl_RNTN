package com.pucp;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	// write your code here

        /*Generate data
        * help.ConvertToStanfordInput("./Model/kaggler_test_processed.tsv", "./Format/kaggler_test_processed_format.tsv");
        * help.ConvertToStanfordInput("./Model/kaggler_dev_processed.tsv", "./Format/kaggler_dev_processed_format.tsv");
        * help.ConvertToStanfordInput("./Model/kaggler_train_processed.tsv", "./Format/kaggler_train_processed_format.tsv");
        * */
        /*Tree bank format
        *help.ConvertToTreeBank("./Format/kaggler_test_processed_format.tsv", "./TreeFormat/kaggler_test_processed_treeformat.tsv");
        *help.ConvertToTreeBank("./Format/kaggler_dev_processed_format.tsv", "./TreeFormat/kaggler_dev_processed_format_treeformat.tsv");
        * help.ConvertToTreeBank("./Format/kaggler_train_processed_format.tsv", "./TreeFormat/kaggler_train_processed_format_treeformat.tsv");
        * */
        /*train model
        *help.TrainModel("./TreeFormat/kaggler_train_processed_format_treeformat.tsv", "./TreeFormat/kaggler_dev_processed_format_treeformat.tsv", "kaggler_v2_model.ser.gz");
        * */
        Helper help = new Helper();







        KagglerController controller = new KagglerController();
        controller.EvaluateModel("kaggler_v2_model.ser.gz", "./Model/kaggler_test_processed.tsv", "./Evals/kaggler_test_v2_model_eval.tsv" );


//        controller.MakeMigration();


        //controller.MigrateToTree_Train();



        //controller.EvaluateModel(null, "kaggler_train.tsv", "kaggler_train_eval.tsv" );
      //  controller.MakeMigration("UseComplexTrainDevStanford");
       // controller.MigrateToTree_Train();
/*
        try{
           KagglerSentimentAnalyzer analyzer = new KagglerSentimentAnalyzer("kaggler_v2_model.ser.gz");
           analyzer.Evaluate("kaggler_test.tsv",  "./KagglerResult/kaggler_test_v2_model.tsv");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
*/
        System.out.println("======================== task finish ===================");


    }
}
