package edu.stanford.cs276;

import edu.stanford.cs276.util.Pair;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import edu.stanford.cs276.util.Dictionary;

public class CandidateGenerator implements Serializable {


  private static int coun = 0;

  private static final long serialVersionUID = 1L;
  private static CandidateGenerator cg_;

  /**
   * Constructor
   * IMPORTANT NOTE: As in the NoisyChannelModel and LanguageModel classes,
   * we want this class to use the Singleton design pattern.  Therefore,
   * under normal circumstances, you should not change this constructor to
   * 'public', and you should not call it from anywhere outside this class.
   * You can get a handle to a CandidateGenerator object using the static
   * 'get' method below.
   */
  private CandidateGenerator() {}

  public static CandidateGenerator get() throws Exception {
    if (cg_ == null) {
      cg_ = new CandidateGenerator();
    }
    return cg_;
  }

  public static final HashSet<Character> alphabet = new HashSet<Character>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
      'u', 'v', 'w', 'x', 'y', 'z', ' ', '\'', '+'));

  public ArrayList<Integer> invalidCount(String query, Dictionary dict) {
    String[] terms = query.split(" ");
    int invalidCount = 0;
    int invalidIndex = 0;
    int index = 0;
    for (String term : terms) {
      if (dict.count(term) == 0) {
        invalidCount++;
        if (invalidCount == 1) {
          invalidIndex = index;
        }
      }
      index++;
    }
    ArrayList<Integer> result = new ArrayList<Integer>();
    result.add(invalidCount);
    result.add(invalidIndex);
    return result;
  }


  public ArrayList<ArrayList<Pair<String,Integer>>> del1(String query, Dictionary dict, int useSpace) {
    ArrayList<ArrayList<Pair<String,Integer>>> resultTuple = new ArrayList<ArrayList<Pair<String,Integer>>>();
    ArrayList<Pair<String,Integer>> result0 = new ArrayList<Pair<String,Integer>>();
    ArrayList<Pair<String,Integer>> result1 = new ArrayList<Pair<String,Integer>>();
    ArrayList<Pair<String,Integer>> result2 = new ArrayList<Pair<String,Integer>>();
    int l = query.length();
    for (int i = 0; i < l; i++) {
      if (!alphabet.contains(query.charAt(i))) {
        continue;
      }
      if (useSpace < 2) {
        if (useSpace == 1) {
          if (query.charAt(i) != ' ') {
            continue;
          }
        } else {
          if (query.charAt(i) == ' ') {
            continue;
          }
        }
      }
      String temp = query.substring(0, i) + query.substring(i + 1);
      ArrayList<Integer> inCount = invalidCount(temp, dict);
      if (inCount.get(0) == 0) {
        result0.add(new Pair<String,Integer>(temp, 0));
      }
      if (inCount.get(0) == 1) {
        result1.add(new Pair<String,Integer>(temp, inCount.get(1)));
      }
      if (inCount.get(0) == 2) {
        result2.add(new Pair<String,Integer>(temp, inCount.get(1)));
      }
    }
    resultTuple.add(result0);
    resultTuple.add(result1);
    resultTuple.add(result2);
    return resultTuple;
  }

  public ArrayList<ArrayList<Pair<String,Integer>>> ins1(String query, Dictionary dict, int useSpace) {
    ArrayList<ArrayList<Pair<String,Integer>>> resultTuple = new ArrayList<ArrayList<Pair<String,Integer>>>();
    ArrayList<Pair<String,Integer>> result0 = new ArrayList<Pair<String,Integer>>();
    ArrayList<Pair<String,Integer>> result1 = new ArrayList<Pair<String,Integer>>();
    ArrayList<Pair<String,Integer>> result2 = new ArrayList<Pair<String,Integer>>();
    int l = query.length();
    for (int i = 0; i <= l; i++) {
      for (char c : alphabet) {
        if (useSpace < 2) {
          if (useSpace == 1) {
            if (c != ' ') {
              continue;
            }
          } else {
            if (c == ' ') {
              continue;
            }
          }
        }
        String temp = query.substring(0, i) + c + query.substring(i);
        ArrayList<Integer> inCount = invalidCount(temp, dict);
        if (inCount.get(0) == 0) {
          result0.add(new Pair<String,Integer>(temp, 0));
        }
        if (inCount.get(0) == 1) {
          result1.add(new Pair<String,Integer>(temp, inCount.get(1)));
        }
        if (inCount.get(0) == 2) {
          result2.add(new Pair<String,Integer>(temp, inCount.get(1)));
        }
      }
    }
    resultTuple.add(result0);
    resultTuple.add(result1);
    resultTuple.add(result2);
    return resultTuple;
  }

  public ArrayList<ArrayList<Pair<String,Integer>>> sub1(String query, Dictionary dict, int useSpace) {
    ArrayList<ArrayList<Pair<String,Integer>>> resultTuple = new ArrayList<ArrayList<Pair<String,Integer>>>();
    ArrayList<Pair<String,Integer>> result0 = new ArrayList<Pair<String,Integer>>();
    ArrayList<Pair<String,Integer>> result1 = new ArrayList<Pair<String,Integer>>();
    ArrayList<Pair<String,Integer>> result2 = new ArrayList<Pair<String,Integer>>();
    int l = query.length();
    for (int i = 0; i < l; i++) {
      if (!alphabet.contains(query.charAt(i))) {
        continue;
      }
      int nonSpace = 0;
      if (useSpace < 2) {
        if (useSpace == 0) {
          if (query.charAt(i) == ' ') {
            continue;
          }
        } else {
          if (query.charAt(i) != ' ') {
            nonSpace++;
          }
        }
      }
      for (char c : alphabet) {
        if (useSpace < 2) {
          if (useSpace == 0) {
            if (c == ' ') {
              continue;
            }
          } else {
            if (c != ' ') {
              nonSpace++;
            }
          }
          if (nonSpace == 2) {
            continue;
          }
        }
        String temp = query.substring(0, i) + c + query.substring(i + 1);
        ArrayList<Integer> inCount = invalidCount(temp, dict);
        if (inCount.get(0) == 0) {
          result0.add(new Pair<String,Integer>(temp, 0));
        }
        if (inCount.get(0) == 1) {
          result1.add(new Pair<String,Integer>(temp, inCount.get(1)));
        }
        if (inCount.get(0) == 2) {
          result2.add(new Pair<String,Integer>(temp, inCount.get(1)));
        }
      }
    }
    resultTuple.add(result0);
    resultTuple.add(result1);
    resultTuple.add(result2);
    return resultTuple;
  }

  public ArrayList<ArrayList<Pair<String,Integer>>> trans1(String query, Dictionary dict, int useSpace) {
    ArrayList<ArrayList<Pair<String,Integer>>> resultTuple = new ArrayList<ArrayList<Pair<String,Integer>>>();
    ArrayList<Pair<String,Integer>> result0 = new ArrayList<Pair<String,Integer>>();
    ArrayList<Pair<String,Integer>> result1 = new ArrayList<Pair<String,Integer>>();
    ArrayList<Pair<String,Integer>> result2 = new ArrayList<Pair<String,Integer>>();
    int l = query.length();
    for (int i = 0; i < l - 1; i++) {
      if (!alphabet.contains(query.charAt(i)) || !alphabet.contains(query.charAt(i + 1))) {
        continue;
      }
      if (useSpace < 2) {
        if (useSpace == 1) {
          if ((query.charAt(i) != ' ' && query.charAt(i + 1) == ' ')) {
            continue;
          }
        } else {
          if ((query.charAt(i) == ' ' || query.charAt(i + 1) == ' ')) {
            continue;
          }
        }
      }
      String temp = query.substring(0, i) + query.charAt(i + 1) + query.charAt(i) + query.substring(i + 2);
      ArrayList<Integer> inCount = invalidCount(temp, dict);
      if (inCount.get(0) == 0) {
        result0.add(new Pair<String,Integer>(temp, 0));
      }
      if (inCount.get(0) == 1) {
        result1.add(new Pair<String,Integer>(temp, inCount.get(1)));
      }
      if (inCount.get(0) == 2) {
        result2.add(new Pair<String,Integer>(temp, inCount.get(1)));
      }
    }
    resultTuple.add(result0);
    resultTuple.add(result1);
    resultTuple.add(result2);
    return resultTuple;
  }

  public void move1AddCandidate(String query, HashSet<String> candidates, Dictionary dict, int useSpace) {
    ArrayList<ArrayList<Pair<String,Integer>>> queryDel1 = del1(query, dict, useSpace);
    for (Pair<String,Integer> pair : queryDel1.get(0)) {
      candidates.add(pair.getFirst());
    }
    ArrayList<ArrayList<Pair<String,Integer>>> queryIns1 = ins1(query, dict, useSpace);
    for (Pair<String,Integer> pair : queryIns1.get(0)) {
      candidates.add(pair.getFirst());
    }
    ArrayList<ArrayList<Pair<String,Integer>>> querySub1 = sub1(query, dict, useSpace);
    for (Pair<String,Integer> pair : querySub1.get(0)) {
      candidates.add(pair.getFirst());
    }
    ArrayList<ArrayList<Pair<String,Integer>>> queryTrans1 = trans1(query, dict, useSpace);
    for (Pair<String,Integer> pair : queryTrans1.get(0)) {
      candidates.add(pair.getFirst());
    }
  }

  public void error1Move1AddCandidate(String query, HashSet<String> candidates, Dictionary dict, int index) {
    String[] terms = query.split(" ");
    String tempQuery = terms[index];
    HashSet<String> tempCandidates = new HashSet<String>();
    move1AddCandidate(tempQuery, tempCandidates, dict, 0);
    StringBuilder sb = new StringBuilder();
    int count = 0;
    for (String t : tempCandidates) {
      terms[index] = t;
      sb.append(t);
      if (count!=index){
        count++;
        sb.append(" ");
      }
      candidates.add(sb.toString());
    }
  }

  public void error2Move1AddCandidate(String query, HashSet<String> candidates, Dictionary dict, int index) {
    String[] terms = query.split(" ");
    String original = terms[index];
    String original2 = terms[index + 1];
    terms[index] = original + original2;
    terms[index + 1] = "";
    String a = "";
    boolean firstTry = true;
    for (String term : terms) {
      if (term.isEmpty()) {
        continue;
      }
      if (!firstTry) {
        a = a + " " + term;
      }
      firstTry = false;
    }
    if (invalidCount(a, dict).get(0) == 0) {
      candidates.add(a);
    }
    for (char c : alphabet) {
      if (c == ' ') {
        continue;
      }
      terms[index] = original + c + original2;
      terms[index + 1] = "";
      a = "";
      for (String term : terms) {
        if (term.isEmpty()) {
          continue;
        }
        if (!firstTry) {
          a = a + " " + term;
        }
        firstTry = false;
      }
      if (invalidCount(a, dict).get(0) == 0) {
        candidates.add(a);
      }
    }
    if (!original2.isEmpty()) {
      terms[index] = original + original2.charAt(0);
      terms[index + 1] = original2.substring(1);
      a = "";
      for (String term : terms) {
        if (term.isEmpty()) {
          continue;
        }
        if (!firstTry) {
          a = a + " " + term;
        }
        firstTry = false;
      }
      if (invalidCount(a, dict).get(0) == 0) {
        candidates.add(a);
      }
    }
    if (!original.isEmpty()) {
      terms[index] = original.substring(0, original.length() - 1);
      terms[index + 1] = original.charAt(original.length() - 1) + original2;
      a = "";
      for (String term : terms) {
        if (term.isEmpty()) {
          continue;
        }
        if (!firstTry) {
          a = a + " " + term;
        }
        firstTry = false;
      }
      if (invalidCount(a, dict).get(0) == 0) {
        candidates.add(a);
      }
    }
  }

  // Generate all candidates for the target query
  public Map<Integer, HashSet<String>> getCandidates(String query, LanguageModel languageModel,
      NoisyChannelModel nsm) throws Exception {
    Map<Integer, HashSet<String>> candidates = new HashMap<Integer, HashSet<String>>();
    candidates.put(0, new HashSet<String>());
    candidates.put(1, new HashSet<String>());
    candidates.put(2, new HashSet<String>());
    // distance = 0
    Dictionary dict = languageModel.dict;
    if (invalidCount(query, dict).get(0) == 0) {
      candidates.get(0).add(query);
    }
    // distance = 1
    ArrayList<ArrayList<Pair<String,Integer>>> queryDel1 = del1(query, dict, 2);
    for (Pair<String,Integer> pair : queryDel1.get(0)) {
      candidates.get(1).add(pair.getFirst());
    }
    ArrayList<ArrayList<Pair<String,Integer>>> queryIns1 = ins1(query, dict, 2);
    for (Pair<String,Integer> pair : queryIns1.get(0)) {
      candidates.get(1).add(pair.getFirst());
    }
    ArrayList<ArrayList<Pair<String,Integer>>> querySub1 = sub1(query, dict, 2);
    for (Pair<String,Integer> pair : querySub1.get(0)) {
      candidates.get(1).add(pair.getFirst());
    }
    ArrayList<ArrayList<Pair<String,Integer>>> queryTrans1 = trans1(query, dict, 2);
    for (Pair<String,Integer> pair : queryTrans1.get(0)) {
      candidates.get(1).add(pair.getFirst());
    }

    // distance = 2,
    // no space operation
    for (Pair<String,Integer> pair : queryDel1.get(0)) {
      move1AddCandidate(pair.getFirst(), candidates.get(2), dict, 2);
    }
    for (Pair<String,Integer> pair : queryDel1.get(1)) {
      error1Move1AddCandidate(pair.getFirst(), candidates.get(2), dict, pair.getSecond());
    }
    for (Pair<String,Integer> pair : queryIns1.get(0)) {
      move1AddCandidate(pair.getFirst(), candidates.get(2), dict, 2);
    }
    for (Pair<String,Integer> pair : queryIns1.get(1)) {
      error1Move1AddCandidate(pair.getFirst(), candidates.get(2), dict, pair.getSecond());
    }
    for (Pair<String,Integer> pair : querySub1.get(0)) {
      move1AddCandidate(pair.getFirst(), candidates.get(2), dict, 2);
    }
    for (Pair<String,Integer> pair : querySub1.get(1)) {
      error1Move1AddCandidate(pair.getFirst(), candidates.get(2), dict, pair.getSecond());
    }
    for (Pair<String,Integer> pair : queryTrans1.get(0)) {
      move1AddCandidate(pair.getFirst(), candidates.get(2), dict, 2);
    }
    for (Pair<String,Integer> pair : queryTrans1.get(1)) {
      error1Move1AddCandidate(pair.getFirst(), candidates.get(2), dict, pair.getSecond());
    }

    // space operation
    for (Pair<String,Integer> pair : queryDel1.get(1)) {
      move1AddCandidate(pair.getFirst(), candidates.get(2), dict, 1);
    }
    for (Pair<String,Integer> pair : queryIns1.get(1)) {
      move1AddCandidate(pair.getFirst(), candidates.get(2), dict, 1);
    }
    for (Pair<String,Integer> pair : querySub1.get(1)) {
      move1AddCandidate(pair.getFirst(), candidates.get(2), dict, 1);
    }
    for (Pair<String,Integer> pair : queryTrans1.get(1)) {
      move1AddCandidate(pair.getFirst(), candidates.get(2), dict, 1);
    }
    for (Pair<String,Integer> pair : queryDel1.get(2)) {
      error2Move1AddCandidate(pair.getFirst(), candidates.get(2), dict, pair.getSecond());
    }
    for (Pair<String,Integer> pair : queryIns1.get(2)) {
      error2Move1AddCandidate(pair.getFirst(), candidates.get(2), dict, pair.getSecond());
    }
    for (Pair<String,Integer> pair : querySub1.get(2)) {
      error2Move1AddCandidate(pair.getFirst(), candidates.get(2), dict, pair.getSecond());
    }
    for (Pair<String,Integer> pair : queryTrans1.get(2)) {
      error2Move1AddCandidate(pair.getFirst(), candidates.get(2), dict, pair.getSecond());
    }
    coun++;
    System.err.println(coun + " " + candidates.get(0).size()+" "+candidates.get(1).size()+" "+candidates.get(2).size());
    
    /*
     * Your code here
     */
    return candidates;
  }

}
