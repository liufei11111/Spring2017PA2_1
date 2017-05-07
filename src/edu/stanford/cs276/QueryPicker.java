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
  static private int counter = 0;
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


//    // Load models from disk
    LanguageModel languageModel = LanguageModel.load();
//    BufferedReader br = new BufferedReader(new FileReader(new File("orderbylengthgold.txt")));
//    String goldterm = null;
//    while((goldterm=br.readLine())!= null){
//      System.out.println("Gold term"+goldterm);
//    }
//    System.out.println();
    NoisyChannelModel nsm = NoisyChannelModel.load();
    BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File(queryFilePath)));
    nsm.setProbabilityType(uniformOrEmpirical);
    if (extra!=null && extra.equals("extra")){
      generateTestFiles( goldFilePath, queryFilePath, languageModel, nsm);
    }
//

    String query = null;

    String goldQuery = null;
    String inputQuery = null;
//    String candSetPerQuery = null;
//    int languageModelScaleingFactorSpace = 100;
//    int smoothingFactor = 50;
    int languageModelScaleingFactorSpace = 10;
    int smoothingFactor = 1;
//    int smoothingFactor = 80;
    for (int i=0;i<languageModelScaleingFactorSpace;++i){

      for (int j=0;j<smoothingFactor;++j) {
        evaludateTestFile( languageModelScaleingFactorSpace,smoothingFactor, goldFilePath,  i,  j,  languageModel, nsm);
//
        System.out.println(i+" out of "+languageModelScaleingFactorSpace+", "+j+" out of "+smoothingFactor +" is done.");
      }
    }
  }
  public static void evaludateTestFile(int iSize,int jSize ,String goldFilePath, int i, int j, LanguageModel languageModel,NoisyChannelModel nsm)
      throws IOException {
    BufferedReader goldFileReader = null;
    if (goldFilePath != null) {
      goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
    }

    Config.languageModelScalingFactor = 0.9+(i+1)*0.1 / iSize ;
//    Config.smoothingFactor = (j+1)*0.1 /jSize;
    String indexI = ""+i;
    String indexJ = ""+j;
    int curr = indexI.length();
    for (int m=0;m<5-curr;++m){
      indexI="0"+indexI;
    }
    curr = indexJ.length();
    for (int m=0;m<5-curr;++m){
      indexJ="0"+indexJ;
    }
    System.out.println("Processing file with index i: "+indexI+",j: "+indexJ);
    FileWriter compareFile = new FileWriter(
        new File("test_result/Cand_gold_diff_" + indexI + "_" +  indexJ + "_" + ".txt"));
    FileWriter compareProbFile = new FileWriter(
        new File("test_result/Cand_gold_diff_Prob_" + indexI + "_" + indexJ + "_" + ".txt"));

    BufferedReader brCanSet = new BufferedReader(new FileReader(new File("Cand_set.txt")));
    QueryPicker qp = new QueryPicker();
    String goldQuery = null;
    String candSetPerQuery = null;
    String originalQuery = null;
    try {
      while (((goldQuery = goldFileReader.readLine()) != null)
          && (candSetPerQuery = brCanSet.readLine()) != null) {
        if (candSetPerQuery.length() == 0 || goldQuery.length() == 0) {
          break;
        }
//        System.out.println(counter++);
        Set<Pair<String, String>> canset = new HashSet<>();
        if (candSetPerQuery.contains(";")) {
          String[] firstParse = candSetPerQuery.split(";");
          originalQuery = firstParse[0];
          if (firstParse[1].contains("%")) {
            String[] secondParse = firstParse[1].split("%");

            for (int k = 0; k < secondParse.length; ++k) {
              String[] pair = secondParse[k].split("-");
              canset.add(new Pair(pair[0], pair[1]));
            }

          } else {
            String[] secondParse = firstParse[1].split("-");
            canset.add(new Pair(secondParse[0], secondParse[1]));
          }

        }else{
          originalQuery = candSetPerQuery;
        }
        canset.add(new Pair(candSetPerQuery,"0"));
        Map<String,HashSet<String>> dummyContainer = new HashMap<>();
        for (Pair<String,String> pair : canset){
          if (dummyContainer.containsKey(pair.getSecond())){
            dummyContainer.get(pair.getSecond()).add(pair.getFirst());
          }else{
            HashSet<String> tmp = new HashSet<>();
            tmp.add(pair.getFirst());
            dummyContainer.put(pair.getSecond(),tmp);
          }
        }
        String chosen = qp.getBestQuery(dummyContainer, languageModel,
            nsm, CandidateGenerator.get(), originalQuery,compareProbFile);
        if (!goldQuery.equals(chosen)) {
          compareFile
              .write("Chosen: " + chosen+", Gold: " + goldQuery + ", Original: " + originalQuery+"\n");
        }


      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    goldFileReader.close();
//    compareProbFile.close();
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

    Map<String,HashSet<String>> candSet = CandidateGenerator.get().getCandidates(inputQuery,languageModel,nsm);
    sb.setLength(0);
    HashSet<String> set = new HashSet<>();
    set.add(inputQuery);
    candSet.put("0",set);

    sb.append(inputQuery+";");

    for (Entry<String,HashSet<String>> entry:candSet.entrySet()){
      for (String str: entry.getValue()){
        sb.append(str);
        sb.append("-");
        sb.append(entry.getKey());
          sb.append("%");
      }
    }

    sb.setLength(sb.length()-1);
    candidateSetFile.write(sb.toString());

    candidateSetFile.write(System.lineSeparator());
  }
      candidateSetFile.close();
}
  public String getBestQuery(Map<String, HashSet<String>> candSet, LanguageModel languageModel,
      NoisyChannelModel nsm, CandidateGenerator canG, String original, FileWriter testInfoWriter) throws Exception {

    String bestCand = original;
    double bestScore = getScoreForOneQuery(nsm,languageModel,original,"0",original);
    StringBuffer candidateBuffer = new StringBuffer();
    candidateBuffer.append(original+"$");
    for (Entry<String,HashSet<String>> entry : candSet.entrySet()){
      String editDisKey = entry.getKey();
      for (String str :entry.getValue()){

        double candScore = getScoreForOneQuery(nsm,languageModel,original,editDisKey,str);
        if (bestCand == null || candScore > bestScore){
          bestCand = str;
          bestScore = candScore;
        }
      }
    }
    if (false){
      testInfoWriter.write(candidateBuffer.toString()+"\n");
    }

    return bestCand;
  }

  private double getScoreForOneQuery(NoisyChannelModel nsm, LanguageModel languageModel, String original,String editDisKey,String  candidate) {
    String[] terms = candidate.split(" " );
    double noisyScore = nsm.getEditCostModel().editProbability(original,candidate,editDisKey);
    double languageScore = languageModel.genLanguageScore(terms);
    return noisyScore+Config.languageModelScalingFactor * languageScore;
  }
}
