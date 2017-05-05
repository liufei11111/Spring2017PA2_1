package edu.stanford.cs276;

import edu.stanford.cs276.util.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by feiliu on 5/4/17.
 */
public class QueryPicker {
  public static void main(String[] args) throws Exception {

    // Parse input arguments
    String uniformOrEmpirical = null;
    String queryFilePath = null;
    String goldFilePath = null;
    String extra = null;
    BufferedReader goldFileReader = null;

    if (args.length == 2) {
      // Default: run without extra credit code or gold data comparison
      uniformOrEmpirical = args[0];
      queryFilePath = args[1];
    } else if (args.length == 3) {
      uniformOrEmpirical = args[0];
      queryFilePath = args[1];
      if (args[2].equals("extra")) {
        extra = args[2];
      } else {
        goldFilePath = args[2];
      }
    } else if (args.length == 4) {
      uniformOrEmpirical = args[0];
      queryFilePath = args[1];
      extra = args[2];
      goldFilePath = args[3];
    } else {
      System.err.println(
          "Invalid arguments.  Argument count must be 2, 3 or 4 \n"
              + "./runcorrector <uniform | empirical> <query file> \n"
              + "./runcorrector <uniform | empirical> <query file> <gold file> \n"
              + "./runcorrector <uniform | empirical> <query file> <extra> \n"
              + "./runcorrector <uniform | empirical> <query file> <extra> <gold file> \n"
              + "SAMPLE: ./runcorrector empirical data/queries.txt \n"
              + "SAMPLE: ./runcorrector empirical data/queries.txt data/gold.txt \n"
              + "SAMPLE: ./runcorrector empirical data/queries.txt extra \n"
              + "SAMPLE: ./runcorrector empirical data/queries.txt extra data/gold.txt \n");
      return;
    }



    // Load models from disk
    LanguageModel languageModel = LanguageModel.load();
    NoisyChannelModel nsm = NoisyChannelModel.load();
    BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File(queryFilePath)));
    nsm.setProbabilityType(uniformOrEmpirical);
//    generateTestFiles( goldFilePath, queryFilePath, languageModel, nsm);
    String query = null;

    String goldQuery = null;
    String inputQuery = null;
    String candSetPerQuery = null;
    int languageModelScaleingFactorSpace = 100;
    int bigramSmoothingFactor = 50;
    for (int i=0;i<languageModelScaleingFactorSpace;++i){

      for (int j=0;j<bigramSmoothingFactor;++j) {
        evaludateTestFile( languageModelScaleingFactorSpace,bigramSmoothingFactor, goldFilePath,  i,  j,  languageModel, nsm);
//

      }
    }
  }
  public static void evaludateTestFile(int iSize,int jSize ,String goldFilePath, int i, int j, LanguageModel languageModel,NoisyChannelModel nsm)
      throws IOException {
    BufferedReader goldFileReader = null;
    if (goldFilePath != null) {
      goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
    }
        //  public static double languageModelScalingFactor = 0.99;
//        public static double smoothingFactor=0.1;
    Config.smoothingFactor = j*1.0 / jSize * (i + 1);
    Config.languageModelScalingFactor = i*4.0 /iSize* (j + 1);
    FileWriter compareFile = new FileWriter(
        new File("test_result/Cand_gold_diff_" + i + "_" + j + "_" + ".txt"));

    BufferedReader brCanSet = new BufferedReader(new FileReader(new File("Cand_set.txt")));
    QueryPicker qp = new QueryPicker();
    String goldQuery = null;
    String candSetPerQuery = null;
    try {
      while (((goldQuery = goldFileReader.readLine()) != null)
          && (candSetPerQuery = brCanSet.readLine()) != null) {
        if (candSetPerQuery.length() == 0 || goldQuery.length() == 0) {
          break;
        }
        Set<Pair<String, Integer>> canset = new HashSet<>();
        String originalQuery = null;
//        System.out.println(
//            "****DEBUG*****goldQuery:" + goldQuery + "\n Candidate query: " + candSetPerQuery
//                + "\n");
        if (candSetPerQuery.contains(";")) {
          String[] firstParse = candSetPerQuery.split(";");
          if (firstParse[1].contains("%")) {
//            System.out.println("FirstParse: " + firstParse[0]);
            String[] secondParse = firstParse[1].split("%");
//            System.out.println("Second" + secondParse[1]);
            //              if ()

            for (int k = 0; k < secondParse.length; ++k) {
              String[] pair = secondParse[k].split("-");
//              System.out.println(secondParse[k]);
//              System.out.println(" Third " + pair[1]);
              canset.add(new Pair<>(pair[0], Integer.parseInt(pair[1])));
            }

          } else {
            String[] secondParse = firstParse[1].split("-");
            canset.add(new Pair<>(secondParse[0], Integer.parseInt(secondParse[1])));
          }
          //
          //          }else{
          String chosen = qp.getBestQuery(canset, languageModel,
              nsm, CandidateGenerator.get(), firstParse[0]);
          if (!chosen.equals(goldQuery)) {
            compareFile
                .write("Chosen: " + ", Gold: " + goldQuery + ", Original: " + firstParse[0]);
          }
          //            continue;
        }
        canset.add(new Pair<>(candSetPerQuery, 0));


      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    goldFileReader.close();
    brCanSet.close();
  }
public static void generateTestFiles(String goldFilePath, String querFile,
    LanguageModel languageModel, NoisyChannelModel nsm) throws  Exception{
    String goldQuery = null;
    String inputQuery = null;
  BufferedReader goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
  BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File(querFile)));
  FileWriter candidateSetFile = new FileWriter(new File("Cand_set.txt"));
  StringBuilder sb = new StringBuilder();
  while(((goldQuery = goldFileReader.readLine()) != null)&&(inputQuery = queriesFileReader.readLine()) != null){

    Map<Integer,HashSet<String>> candSet = CandidateGenerator.get().getCandidates(inputQuery,languageModel,nsm);
    sb.setLength(0);


    sb.append(inputQuery+";");

    for (Entry<Integer,HashSet<String>> entry:candSet.entrySet()){
//      int i=0;
      for (String str: entry.getValue()){
        sb.append(str);
        sb.append("-");
        sb.append(entry.getKey());
//        if (i<entry.getValue().size()){
          sb.append("%");
//          i++;
//        }
      }
//      sb.append(System.lineSeparator());
//      System.out.println(goldQuery);
//      Thread.sleep(1000);

//      System.out.print(sb.toString());
    }

    sb.setLength(sb.length()-1);
//    sb.append(";"+goldQuery)
    candidateSetFile.write(sb.toString());

    candidateSetFile.write(System.lineSeparator());
  }
      candidateSetFile.close();
}
  public String getBestQuery(Set<Pair<String,Integer>> candSet, LanguageModel languageModel,
      NoisyChannelModel nsm, CandidateGenerator canG, String original) throws Exception {
    Pair<String,Double> bestCand = null;
    for (Pair<String,Integer> str : candSet){
      String[] terms = str.getFirst().split(" " );
      double noisyScore = nsm.getEditCostModel().editProbability(original,str.getFirst(),str.getSecond());
      double languageScore = languageModel.genLanguageScore(terms);
      double candScore = noisyScore+Config.languageModelScalingFactor * languageScore;
      if (bestCand == null){
        bestCand = new Pair<>(str.getFirst(),candScore);
      }else if (candScore > bestCand.getSecond()){
        bestCand.setFirst(str.getFirst());
        bestCand.setSecond(candScore);
      }

    }
    return bestCand.getFirst();
  }

//  public Pair<String,double[]> getCorrectedQuery(String original, Map<String,Pair<Double,Integer>> queries,NoisyChannelModel ncm, LanguageModel lm) {
//    Pair<String, double[]> thePair = null;
//    for (Entry<String,Pair<Double, Integer>> query: queries.entrySet()){
//      // everything is already log transformed
////      double noisyScore = ncm.getEditCostModel().editProbability(original,query.getKey(),query.getValue().getSecond());
////      double languageScore = query.getValue().getFirst();
////      double candScore = noisyScore+Config.languageModelScalingFactor * languageScore;
////
////      if (thePair == null){
////        thePair = new Pair<>(query.getKey(),candScore);
////      }else if (thePair.getSecond()<candScore){
////        thePair = new Pair<>(query.getKey(),candScore);
////      }
//      double noisyScore = ncm.getEditCostModel().editProbability(original,query.getKey(),query.getValue().getSecond());
//      double languageScore = query.getValue().getFirst();
//      double candScore = noisyScore+Config.languageModelScalingFactor * languageScore;
//      double[] scores = new double[3];
//      scores[0]=noisyScore;
//      scores[1]=languageScore;
//      scores[2]=candScore;
//      if (thePair == null){
//        thePair = new Pair<>(query.getKey(),scores);
//      }else if (thePair.getSecond()[2]<candScore){
//        thePair = new Pair<>(query.getKey(),scores);
//      }
//    }
//    if (thePair == null){
//      throw new RuntimeException("Forbidden query cands without a single result!");
//    }
//    // TODO: delete this test section!!!!!
//    List<Entry<String,Pair<Double,Integer>>> list = new ArrayList<>();
//    Collections.sort(list, new Comparator<Entry<String, Pair<Double, Integer>>>() {
//      @Override
//      public int compare(Entry<String, Pair<Double, Integer>> o1,
//          Entry<String, Pair<Double, Integer>> o2) {
//        return o1.getValue().getFirst().compareTo(o2.getValue().getFirst());
//      }
//    });
//    for (Entry<String,Pair<Double,Integer>> entry : list){
//      System.out.println(entry.getKey()+": "+entry.getValue());
//    }
//    // TODO: delete this test section!!!!!
//    return thePair;
//  }
}
