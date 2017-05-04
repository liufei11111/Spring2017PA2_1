package edu.stanford.cs276;

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
import java.util.List;
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

  Dictionary dictionary = new Dictionary();

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
  }

  /**
   * This method is called by the constructor, and computes language model parameters 
   * (i.e. counts of unigrams, bigrams, etc.), which are then stored in the class members
   * declared above.  
   */
  public void constructDictionaries(String corpusFilePath) throws Exception {

    System.out.println("Constructing dictionaries...");
    File dir = new File(corpusFilePath);
    for (File file : dir.listFiles()) {
      if (".".equals(file.getName()) || "..".equals(file.getName())) {
        continue; // Ignore the self and parent aliases.
      }
      System.out.printf("Reading data file %s ...\n", file.getName());
      BufferedReader input = new BufferedReader(new FileReader(file));

      Set<Character> allChar = new HashSet<>();

      String line = null;
      while ((line = input.readLine()) != null) {
        /*
         * Remember: each line is a document (refer to PA2 handout)
         *
         */
        line= line.replaceAll("_+"," ");
        line = line.replaceAll("\\s+"," ");
        String[] tokens = line.trim().split(" ");
        for (int i=0;i<tokens.length;++i){
          boolean foundNum = containsNumber(tokens[i],allChar);
          if (foundNum){continue;}
          if (i!=tokens.length-1){
            Pair<String,String> bigramPair = new Pair<>(tokens[i],tokens[i+1]);
            if (dictionary.bigram.containsKey(bigramPair)){
              dictionary.bigram.put(bigramPair,dictionary.bigram.get(bigramPair)+1);
            }else{
              dictionary.bigram.put(bigramPair,1);
            }
          }
          if (dictionary.unigram.containsKey(tokens[i])){
            dictionary.unigram.put(tokens[i],dictionary.unigram.get(tokens[i])+1);

          }else{
            dictionary.unigram.put(tokens[i],1);
          }
          dictionary.termCount++;
        }

      }
      FileWriter fw = new FileWriter(new File("unigram.txt"));
      FileWriter fw1 = new FileWriter(new File("nonExistenceChars.txt"));
      FileWriter fw2 = new FileWriter(new File("completeChars.txt"));
      FileWriter fw3 = new FileWriter(new File("orderbylength.txt"));
      FileWriter fw4 = new FileWriter(new File("orderbylengthgold.txt"));
      List<Entry<String,Integer>> list = new ArrayList<>(dictionary.unigram.entrySet());
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
      Set<Character> goldCharSet = new HashSet<>();
      BufferedReader gold = new BufferedReader(new FileReader("data/dev_set/gold.txt"));
      HashMap<String, Integer> goldDic = new HashMap<>();
      while ((line = input.readLine())!= null){
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
      list = new ArrayList<>(goldDic.entrySet());
      Collections.sort(list, new Comparator<Entry<String, Integer>>() {
        @Override
        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
          return o2.getValue().compareTo(o1.getValue());
        }
      });
      for (Entry<String,Integer> entry:list){
        fw4.write(entry.getKey()+","+entry.getValue()+"\n");
      }
      for(Character oneChar : allChar){
        fw2.write(oneChar+"\n");
      }
      allChar.removeAll(goldCharSet);
      for (Character ch : allChar){
        fw1.write(ch+"\n");
      }
      fw1.close();
      fw2.close();
      fw3.close();
        gold.close();
      input.close();
    }
    System.out.println("Done.");
  }

  private boolean containsNumber(String str, Set<Character> allChar) {
    boolean foundNum = false;
    for (char aChar : str.toCharArray()){
      if (Character.isDigit(aChar)){
        foundNum=true;
        break;
      }
      // TODO remove test
      allChar.add(aChar);
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
      }
    } catch (Exception e) {
      throw new Exception("Unable to load language model.  You may not have run buildmodels.sh!");
    }
    return lm_;
  }

  /**
   * Saves the object (and all associated data) to disk
   */
  public void save() throws Exception {
    FileOutputStream saveFile = new FileOutputStream(Config.languageModelFile);
    ObjectOutputStream save = new ObjectOutputStream(saveFile);
    save.writeObject(this);
    save.close();
  }

  public String pickTopCandidate(Set<String> candidateQuery, NoisyChannelModel nsm) {
    return null;
  }
}
