package edu.stanford.cs276.util;

import edu.stanford.cs276.LanguageModel;
import edu.stanford.cs276.NoisyChannelModel;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Dictionary implements Serializable {

  public int termCount;
  public  HashMap<String, Integer> unigram;
  public  HashMap<Pair<String,String>, Integer> bigram;
  public int termCount() {
    return termCount;
  }

  public Dictionary() {
    termCount = 0;
    unigram = new HashMap<>();
    bigram = new HashMap<>();
  }

  public void add(String term) {
    termCount++;
    if (unigram.containsKey(term)) {
      unigram.put(term, unigram.get(term) + 1);
    } else {
      unigram.put(term, 1);
    }
  }

  public int count(String term) {
    if (unigram.containsKey(term)) {
      return unigram.get(term);
    } else {
      return 0;
    }
  }

  public double logUnigramProb(String term){
    return 0.0;
  }
  public double logbigramConditionalProb(String term1, String term2){
    return 0.0;
  }

  public double jointProbability(List<String> term){
    return 0.0;
  }

}
