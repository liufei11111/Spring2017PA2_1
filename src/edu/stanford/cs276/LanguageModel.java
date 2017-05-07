package edu.stanford.cs276;

import edu.stanford.cs276.util.MapEncoder;
import edu.stanford.cs276.util.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.stanford.cs276.util.Dictionary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * LanguageModel class constructs a language model from the training corpus.
 * This model will be used to score generated query candidates.
 * 
 * This class uses the Singleton design pattern
 * (https://en.wikipedia.org/wiki/Singleton_pattern).
 */
public class LanguageModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
  private static LanguageModel lm_;

  public Dictionary dict = new Dictionary();
  private static String[] stopWords = {"on","a","an","and","are","as","at","be","by","for","from","has","he","in","is","it","its","of","that","the","to","was","were","will","with"};

  //  private static String[] stopWords = {"the"};
  private static String[] lengthOneWhiteList = {};
  private static String[] lengthTwoWhiteList = { };

  private static Set<String> whiteListSet=new HashSet<>();
  private static Set<String> balckListSet=new HashSet<>();
  /*
   * Feel free to add more members here (e.g., a data structure that stores bigrams)
   */

  /**
   * Constructor
   * IMPORTANT NOTE: you should NOT change the access level for this constructor to 'public', 
   * and you should NOT call this constructor outside of this class.  This class is intended
   * to follow the "Singleton" design pattern, which ensures that there is only ONE object of
   * this type in existence at any time.  In most circumstances, you should get a handle to a 
   * NoisyChannelModel object by using the static 'create' and 'load' methods below, which you
   * should not need to modify unless you are making substantial changes to the architecture
   * of the starter code.  
   *
   * For more info about the Singleton pattern, see https://en.wikipedia.org/wiki/Singleton_pattern.  
   */
  private LanguageModel(String corpusFilePath) throws Exception {
    constructDictionaries(corpusFilePath);

    for (String str:lengthOneWhiteList){
      whiteListSet.add(str);
    }
    for (String str:lengthTwoWhiteList){
      whiteListSet.add(str);
    }
    for(String str : stopWords){
      balckListSet.add(str);
    }
  }

  /**
   * This method is called by the constructor, and computes language model parameters 
   * (i.e. counts of unigrams, bigrams, etc.), which are then stored in the class members
   * declared above.  
   */
  public void constructDictionaries(String corpusFilePath) throws Exception {

    System.out.println("Constructing dictionaries...");
    File dir = new File(corpusFilePath);
    FileWriter fw = new FileWriter(new File("unigram.txt"));
    FileWriter fw1 = new FileWriter(new File("nonExistenceChars.txt"));
    FileWriter fw2 = new FileWriter(new File("completeChars.txt"));
    FileWriter fw3 = new FileWriter(new File("orderbylength.txt"));
    FileWriter fw4 = new FileWriter(new File("orderbyCountgold.txt"));
    FileWriter fw6 = new FileWriter(new File("orderbyWordLengold.txt"));
    FileWriter fw5 = new FileWriter(new File("wordsNotCoveredInGold.txt"));

    int count = 0;

    for (File file : dir.listFiles()) {
      if (".".equals(file.getName()) || "..".equals(file.getName())) {
        continue; // Ignore the self and parent aliases.
      }
      System.out.printf("Reading data file %s ...\n", file.getName());
      BufferedReader input = new BufferedReader(new FileReader(file));

//      Set<Character> allChar = new HashSet<>();

      String line = null;
      while ((line = input.readLine()) != null) {
        /*
         * Remember: each line is a document (refer to PA2 handout)
         *
         */

        List<String> tokens = purgeLine(line.trim().split(" "));
        for (int i=0;i<tokens.size();++i){
          boolean foundNum = containsNumberTooShortOrTooLong(tokens.get(i));
          String thieToken = tokens.get(i);
          if (foundNum){continue;}
          if (i!=tokens.size()-1&&!containsNumberTooShortOrTooLong(tokens.get(i+1))){
            Pair<String,String> bigramPair = new Pair<>(tokens.get(i),tokens.get(i+1));
            if (dict.bigram.containsKey(bigramPair)){
              dict.bigram.put(bigramPair, dict.bigram.get(bigramPair)+1);
            }else{
              dict.bigram.put(bigramPair,1);
            }
          }
          if (dict.unigram.containsKey(tokens.get(i))){
            dict.unigram.put(tokens.get(i), dict.unigram.get(tokens.get(i))+1);

          }else{
            dict.unigram.put(tokens.get(i),1);
          }
        }

      }

      for(Entry<String,Integer> entry : dict.unigram.entrySet()){
        count+=entry.getValue();
      }

    }
    // end of directory level
    System.out.println("Total word count is : "+count);
    List<Entry<String,Integer>> list = new ArrayList<>(dict.unigram.entrySet());
    Collections.sort(list, new Comparator<Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return o2.getValue().compareTo(o1.getValue());
      }
    });
    for (int i=0;i<list.size();++i){
      fw.write(list.get(i).getKey()+","+list.get(i).getValue()+"\n");
    }
    fw.close();
    Collections.sort(list, new Comparator<Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return o2.getKey().length()-(o1.getKey().length());
      }
    });
    for (int i=0;i<list.size();++i){
      fw3.write(list.get(i).getKey()+","+list.get(i).getValue()+"\n");
    }
    fw3.close();


    // Gold generation
    Set<Character> goldCharSet = new HashSet<>();
    BufferedReader gold = new BufferedReader(new FileReader("data/dev_set/gold.txt"));
    HashMap<String, Integer> goldDic = new HashMap<>();
    String line = null;
    int goldLines = 0;
    while ((line = gold.readLine())!= null){
      goldLines++;
      for (String str : line.split(" ")){
        if (goldDic.containsKey(str)){
          goldDic.put(str,goldDic.get(str)+1);
        }else{
          goldDic.put(str,1);
        }
      }
      for (char aChar : line.toCharArray()){
        goldCharSet.add(aChar);
      }
    }
    System.out.println("number of lines read for gold.txt: "+goldLines);
    List<Entry<String,Integer>>listOfGolds = new ArrayList<>(goldDic.entrySet());
    Collections.sort(listOfGolds, new Comparator<Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return o2.getValue().compareTo(o1.getValue());
      }
    });
    for (Entry<String,Integer> entry:listOfGolds){
      fw4.write(entry.getKey()+","+entry.getValue()+"\n");
    }
    Collections.sort(listOfGolds, new Comparator<Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return o2.getKey().length()-o1.getKey().length();
      }
    });
    for (Entry<String,Integer> entry:listOfGolds){
      fw6.write(entry.getKey()+","+entry.getValue()+"\n");
    }
    Set<String> setNotInGoldInDIc = goldDic.keySet();
    setNotInGoldInDIc.removeAll(dict.unigram.keySet());
    for (String str : setNotInGoldInDIc){
      fw5.write(str+"\n");
    }

    fw1.close();
    fw2.close();
    fw3.close();
    gold.close();
    fw4.close();
    fw5.close();
    fw6.close();
    System.out.println("Done.");
  }

  private List<String> purgeLine(String[] split) {
    List<String> candsList = new ArrayList<>();
    for (String str : split){
      String  line= str.replaceAll("_+"," ");
      line = line.replaceAll("\\s+"," ");
      if (line.equals(" ")||line.isEmpty()){
        continue;
      }
      if (!line.contains(" ")){

        candsList.addAll(tearForNumber(line));
      }else{
        String[] cands = line.split(" ");
        for (String cand : cands){
          candsList.addAll(tearForNumber(line));
        }

      }

    }
    return candsList;
  }
  private List<String> tearForNumber(String str){
    str.replaceAll(""," ");
    str.replaceAll(" +"," ");
    String[] terms = str.split(" ");
    List<String> list = new LinkedList<>();
    for (String term : terms){
      list.add(term);
    }
    return list;
  }
  private boolean containsNumberTooShortOrTooLong(String str) {
    int len = str.length();
    if (len>Config.wordThreshold||len==0){return true;}
    boolean foundNum = false;
    for (char aChar : str.toCharArray()){
      if (Character.isDigit(aChar)){
        foundNum=true;
        break;
      }
    }
    return foundNum;
  }

  /**
   * Creates a new LanguageModel object from a corpus. This method should be used to create a
   * new object rather than calling the constructor directly from outside this class
   */
  public static LanguageModel create(String corpusFilePath) throws Exception {
    if (lm_ == null) {
      lm_ = new LanguageModel(corpusFilePath);
    }
    return lm_;
  }

  /**
   * Loads the language model object (and all associated data) from disk
   */
  public static LanguageModel load() throws Exception {
    try {
      if (lm_ == null) {
        FileInputStream fiA = new FileInputStream(Config.languageModelFile);
        ObjectInputStream oisA = new ObjectInputStream(fiA);
        lm_ = (LanguageModel) oisA.readObject();
        MapEncoder me = new MapEncoder();
        Map<String, Integer> unigram = me.retrieveUnigram(lm_);
        Map<Pair<String,String>,Integer> bigram = me.retrieveBigram();
        lm_.dict.unigram=unigram;
        lm_.dict.bigram=bigram;
      }
    } catch (Exception e) {
      throw e;
//      throw new Exception("Unable to load language model.  You may not have run buildmodels.sh!");
    }
    return lm_;
  }

  /**
   * Saves the object (and all associated data) to disk
   */
  public void save() throws Exception {
    FileOutputStream saveFile = new FileOutputStream(Config.languageModelFile);
    ObjectOutputStream save = new ObjectOutputStream(saveFile);

    MapEncoder me = new MapEncoder();
    me.saveUnigram(dict.unigram);
    me.saveBigram(dict.bigram);
    dict.unigram = null;
    dict.bigram = null;
    save.writeObject(this);
    save.close();
  }



  public double genLanguageScore(String[] terms) {
    return jointProbScore(terms);
  }
  public double unigramProbForTerm(String term) {
    Integer count = dict.unigram.get(term);
    if (count == null){
      return Math.log(Config.eps);
    }else{
//
//      if (
//          ( term.length()==1 || balckListSet.contains(term) )
//              &&!whiteListSet.contains(term)
//          ){
//        double rescaled = Math.log(count);
//        return Math.log(rescaled)-Math.log(dict.termCount);
//      }
      return Math.log(count)-Math.log(dict.termCount);
    }
  }
  public double getConditionalProd(String[] terms, int index) {
    if (index<0||index>=terms.length){return Double.NaN;}
    double unigramScore = unigramProbForTerm(terms[index]);
    if (index == 0){
      double termUnigram = rawCountForTerm(terms[index]);
      double countBigram = rawBiCountForTerms(terms[index],terms[index+1]);
      double bigramScore = Math.log(countBigram+Config.eps)-Math.log(termUnigram+Config.eps);
      return unigramScore+unigramScore*Config.smoothingFactor+bigramScore*(1-Config.smoothingFactor);
    }

    double termUnigram = rawCountForTerm(terms[index]);
    double countBigram = rawBiCountForTerms(terms[index],terms[index+1]);
    double bigramScore = Math.log(countBigram+Config.eps)-Math.log(termUnigram+Config.eps);
    // we use bigram to decide which word is wrong. and we just need to log of count and total is constant and can be ignored
    return  unigramScore*Config.smoothingFactor+bigramScore*(1-Config.smoothingFactor);
  }


  private double rawBiCountForTerms(String term1, String term2) {
    Integer count = dict.bigram.get(new Pair<>(term1,term2));
    if (count == null){
      return 0.0;
    }else{
      return count;
    }
  }

  private double rawCountForTerm(String term1) {
    Integer count = dict.unigram.get(term1);
    if (count == null){
      return 0.0;
    }else{
      return count;
    }
  }

  public  double jointProbScore(String[] terms) {
//    String[] terms = query.split(" ");
    double logLanguageScore = 0.0; // log 1
    for(int i=0;i<terms.length-1;++i){
        // raw count is good as the total count is a constant
        logLanguageScore += getConditionalProd(terms,i);

    }
    return logLanguageScore;
  }
}
