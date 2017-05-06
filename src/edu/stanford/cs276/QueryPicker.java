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
    if (extra!=null && extra.equals("extra")){
      generateTestFiles( goldFilePath, queryFilePath, languageModel, nsm);
    }
//

    String query = null;

    String goldQuery = null;
    String inputQuery = null;
//    String candSetPerQuery = null;
//    int languageModelScaleingFactorSpace = 100;
//    int editProdSpace = 50;
    int languageModelScaleingFactorSpace = 100;
    int editProdSpace = 1;
//    int editProdSpace = 80;
    for (int i=0;i<languageModelScaleingFactorSpace;++i){

      for (int j=0;j<editProdSpace;++j) {
        evaludateTestFile( languageModelScaleingFactorSpace,editProdSpace, goldFilePath,  i,  j,  languageModel, nsm);
//
        System.out.println(i+" out of "+languageModelScaleingFactorSpace+", "+j+" out of "+editProdSpace +" is done.");
      }
    }
  }
  public static void evaludateTestFile(int iSize,int jSize ,String goldFilePath, int i, int j, LanguageModel languageModel,NoisyChannelModel nsm)
      throws IOException {
    BufferedReader goldFileReader = null;
    if (goldFilePath != null) {
      goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
    }

    Config.languageModelScalingFactor = (j+1)*0.01 / jSize ;
//    Config.singleEditProb = (i+1)*0.1 /iSize;
    FileWriter compareFile = new FileWriter(
        new File("test_result/Cand_gold_diff_" + i + "_" + j + "_" + ".txt"));
    FileWriter compareProbFile = new FileWriter(
        new File("test_result/Cand_gold_diff_Prob_" + i + "_" + j + "_" + ".txt"));

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
        Set<Pair<String, Integer>> canset = new HashSet<>();
        if (candSetPerQuery.contains(";")) {
          String[] firstParse = candSetPerQuery.split(";");
          originalQuery = firstParse[0];
          if (firstParse[1].contains("%")) {
            String[] secondParse = firstParse[1].split("%");

            for (int k = 0; k < secondParse.length; ++k) {
              String[] pair = secondParse[k].split("-");
              canset.add(new Pair<>(pair[0], Integer.parseInt(pair[1])));
            }

          } else {
            String[] secondParse = firstParse[1].split("-");
            canset.add(new Pair<>(secondParse[0], Integer.parseInt(secondParse[1])));
          }

        }else{
          originalQuery = candSetPerQuery;
        }
        canset.add(new Pair<>(candSetPerQuery,0));

        String chosen = qp.getBestQuery(canset, languageModel,
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

    Map<Integer,HashSet<String>> candSet = CandidateGenerator.get().getCandidates(inputQuery,languageModel,nsm);
    sb.setLength(0);


    sb.append(inputQuery+";");

    for (Entry<Integer,HashSet<String>> entry:candSet.entrySet()){
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
  public String getBestQuery(Set<Pair<String,Integer>> candSet, LanguageModel languageModel,
      NoisyChannelModel nsm, CandidateGenerator canG, String original, FileWriter testInfoWriter) throws Exception {
    Pair<String,Double> bestCand = null;
    StringBuffer candidateBuffer = new StringBuffer();
    candidateBuffer.append(original+"$");
    for (Pair<String,Integer> str : candSet){
      String[] terms = str.getFirst().split(" " );
      double noisyScore = nsm.getEditCostModel().editProbability(original,str.getFirst(),str.getSecond());
      double languageScore = languageModel.genLanguageScore(terms);
      double candScore = noisyScore+Config.languageModelScalingFactor * languageScore;
      candidateBuffer.append(str+":: <"+languageScore+","+noisyScore+","+candScore+">|");
      if (bestCand == null){
        bestCand = new Pair<>(str.getFirst(),candScore);
      }else if (candScore > bestCand.getSecond()){
        bestCand.setFirst(str.getFirst());
        bestCand.setSecond(candScore);
      }

    }
    testInfoWriter.write(candidateBuffer.toString()+"\n");
    return bestCand.getFirst();
  }
}
