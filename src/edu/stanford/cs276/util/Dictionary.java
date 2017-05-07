package edu.stanford.cs276.util;

import edu.stanford.cs276.LanguageModel;
import edu.stanford.cs276.NoisyChannelModel;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dictionary implements Serializable {

  public int termCount;
  public Map<String, Integer> unigram;
  public Map<Pair<String,String>, Integer> bigram;

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

}
