package edu.stanford.cs276;

import edu.stanford.cs276.util.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    if (goldFilePath != null) {
      goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
    }

    // Load models from disk
    LanguageModel languageModel = LanguageModel.load();
    NoisyChannelModel nsm = NoisyChannelModel.load();
    BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File(queryFilePath)));
    nsm.setProbabilityType(uniformOrEmpirical);

    String query = null;

    String goldQuery = null;
    String inputQuery = null;
    FileWriter compareFile = new FileWriter(new File("Cand_gold_diff.txt"));
    FileWriter candidateSetFile = new FileWriter(new File("Cand_set.txt"));
//    while(((goldQuery = goldFileReader.readLine()) != null)||(inputQuery = queriesFileReader.readLine()) != null){
    StringBuilder sb = new StringBuilder();
    while((inputQuery = queriesFileReader.readLine()) != null){
      Set<String> candSet = CandidateGenerator.get().getCandidates(inputQuery,languageModel,nsm);
      sb.setLength(0);
      sb.append(inputQuery+"$$$");
      int i=0;
      for (String str: candSet){
        sb.append(str);
        sb.append(str);
        if (i<candSet.size()-1){
          sb.append("|||");
          i++;
        }
      }
      candidateSetFile.write(sb.toString()+"\n");
//      String choisenCand = QueryPicker.getBestQuery(candSet,languageModel,nsm);
    }
    compareFile.close();
    candidateSetFile.close();
    goldFileReader.close();
    queriesFileReader.close();
  }

  public String getBestQuery(Set<String> candSet, LanguageModel languageModel,
      NoisyChannelModel nsm, CandidateGenerator canG, String original) throws Exception {
    Pair<String,Double> bestCand = null;
    for (String str : candSet){
      String[] terms = str.split(" " );
      double noisyScore = nsm.getEditCostModel().editProbability(original,str,terms.length);
      double languageScore = languageModel.genLanguageScore(terms,original);
      double candScore = noisyScore+Config.languageModelScalingFactor * languageScore;
      if (bestCand == null){
        bestCand = new Pair<>(str,candScore);
      }else if (candScore > bestCand.getSecond()){
        bestCand.setFirst(str);
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
