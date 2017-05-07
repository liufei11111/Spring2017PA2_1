package edu.stanford.cs276;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    ArrayList<Integer> result = new ArrayList<Integer>();
    String[] terms = query.split(" ", -1);
    int invalidCount = 0;
    int index = 0;
    for (String term : terms) {
      if (dict.count(term) == 0) {
        invalidCount++;
        result.add(index);
      }
      index++;
    }
    result.add(0, invalidCount);
    return result;
  }

  public ArrayList<ArrayList<String>> del1(String query, Dictionary dict, int useSpace, boolean isSimple) {
    ArrayList<ArrayList<String>> resultTuple = new ArrayList<ArrayList<String>>();
    ArrayList<String> result0 = new ArrayList<String>();
    ArrayList<String> result1 = new ArrayList<String>();
    ArrayList<String> result2 = new ArrayList<String>();
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
      if (!isSimple) {
        ArrayList<Integer> inCount = invalidCount(temp, dict);
        if (inCount.get(0) == 0) {
          result0.add(temp);
        }
        if (inCount.get(0) == 1) {
          result1.add(temp);
        }
        if (inCount.get(0) == 2) {
          result2.add(temp);
        }
      } else {
        result0.add(temp);
      }
    }
    resultTuple.add(result0);
    resultTuple.add(result1);
    resultTuple.add(result2);
    return resultTuple;
  }

  public ArrayList<ArrayList<String>> ins1(String query, Dictionary dict, int useSpace, boolean isSimple) {
    ArrayList<ArrayList<String>> resultTuple = new ArrayList<ArrayList<String>>();
    ArrayList<String> result0 = new ArrayList<String>();
    ArrayList<String> result1 = new ArrayList<String>();
    ArrayList<String> result2 = new ArrayList<String>();
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
        if (!isSimple) {
          ArrayList<Integer> inCount = invalidCount(temp, dict);
          if (inCount.get(0) == 0) {
            result0.add(temp);
          }
          if (inCount.get(0) == 1) {
            result1.add(temp);
          }
          if (inCount.get(0) == 2) {
            result2.add(temp);
          }
        } else {
          result0.add(temp);
        }
      }
    }
    resultTuple.add(result0);
    resultTuple.add(result1);
    resultTuple.add(result2);
    return resultTuple;
  }

  public ArrayList<ArrayList<String>> sub1(String query, Dictionary dict, int useSpace, boolean isSimple) {
    ArrayList<ArrayList<String>> resultTuple = new ArrayList<ArrayList<String>>();
    ArrayList<String> result0 = new ArrayList<String>();
    ArrayList<String> result1 = new ArrayList<String>();
    ArrayList<String> result2 = new ArrayList<String>();
    int l = query.length();
    for (int i = 0; i < l; i++) {
      if (!alphabet.contains(query.charAt(i))) {
        continue;
      }
      int nonSpace1 = 0;
      if (useSpace < 2) {
        if (useSpace == 0) {
          if (query.charAt(i) == ' ') {
            continue;
          }
        } else {
          if (query.charAt(i) != ' ') {
            nonSpace1++;
          }
        }
      }
      for (char c : alphabet) {
        int nonSpace2 = 0;
        if (useSpace < 2) {
          if (useSpace == 0) {
            if (c == ' ') {
              continue;
            }
          } else {
            if (c != ' ') {
              nonSpace2++;
            }
            if (nonSpace1 + nonSpace2 == 2) {
              continue;
            }
          }
        }
        if (c == query.charAt(i)) {
          continue;
        }
        String temp = query.substring(0, i) + c + query.substring(i + 1);
        if (!isSimple) {
          ArrayList<Integer> inCount = invalidCount(temp, dict);
          if (inCount.get(0) == 0) {
            result0.add(temp);
          }
          if (inCount.get(0) == 1) {
            result1.add(temp);
          }
          if (inCount.get(0) == 2) {
            result2.add(temp);
          }
        } else {
          result0.add(temp);
        }
      }
    }
    resultTuple.add(result0);
    resultTuple.add(result1);
    resultTuple.add(result2);
    return resultTuple;
  }

  public ArrayList<ArrayList<String>> trans1(String query, Dictionary dict, int useSpace, boolean isSimple) {
    ArrayList<ArrayList<String>> resultTuple = new ArrayList<ArrayList<String>>();
    ArrayList<String> result0 = new ArrayList<String>();
    ArrayList<String> result1 = new ArrayList<String>();
    ArrayList<String> result2 = new ArrayList<String>();
    int l = query.length();
    for (int i = 0; i < l - 1; i++) {
      if (!alphabet.contains(query.charAt(i)) || !alphabet.contains(query.charAt(i + 1))) {
        continue;
      }
      if (useSpace < 2) {
        if (useSpace == 1) {
          if ((query.charAt(i) != ' ' && query.charAt(i + 1) != ' ')) {
            continue;
          }
        } else {
          if ((query.charAt(i) == ' ' || query.charAt(i + 1) == ' ')) {
            continue;
          }
        }
      }
      if (query.charAt(i) == query.charAt(i + 1)) {
        continue;
      }
      String temp = query.substring(0, i) + query.charAt(i + 1) + query.charAt(i) + query.substring(i + 2);
      if (!isSimple) {
        ArrayList<Integer> inCount = invalidCount(temp, dict);
        if (inCount.get(0) == 0) {
          result0.add(temp);
        }
        if (inCount.get(0) == 1) {
          result1.add(temp);
        }
        if (inCount.get(0) == 2) {
          result2.add(temp);
        }
      } else {
        result0.add(temp);
      }
    }
    resultTuple.add(result0);
    resultTuple.add(result1);
    resultTuple.add(result2);
    return resultTuple;
  }

  public void move1AddCandidate(String query, HashMap<Integer, HashSet<String>> candidates, Dictionary dict, int useSpace, int reIndex, int oper) {
    switch(oper) {
      case 0:
        ArrayList<ArrayList<String>> queryDel1 = del1(query, dict, useSpace, false);
        for (int i = 0; i <= reIndex; i++) {
          for (String qp : queryDel1.get(i)) {
            candidates.get(i).add(qp);
          }
        }
        break;
      case 1:
        ArrayList<ArrayList<String>> queryIns1 = ins1(query, dict, useSpace, false);
        for (int i = 0; i <= reIndex; i++) {
          for (String qp : queryIns1.get(i)) {
            candidates.get(i).add(qp);
          }
        }
        break;
      case 2:
        ArrayList<ArrayList<String>> querySub1 = sub1(query, dict, useSpace, false);
        for (int i = 0; i <= reIndex; i++) {
          for (String qp : querySub1.get(i)) {
            candidates.get(i).add(qp);
          }
        }
        break;
      case 3:
        ArrayList<ArrayList<String>> queryTrans1 = trans1(query, dict, useSpace, false);
        for (int i = 0; i <= reIndex; i++) {
          for (String qp : queryTrans1.get(i)) {
            candidates.get(i).add(qp);
          }
        }
        break;
    }
  }

  public void operateOnce(String query, ArrayList<Integer> parseResult, Dictionary dict, HashSet<String> candidate, int oper) {
    int errorNum = parseResult.get(0);
    String[] terms = query.split(" ", -1);
    if (errorNum == 1) {
      int errorIndex = parseResult.get(1);
      String tempQuery = terms[errorIndex];
      HashMap<Integer, HashSet<String>> tempCandidates = new HashMap<Integer, HashSet<String>>();
      tempCandidates.put(0, new HashSet<String>());
      move1AddCandidate(tempQuery, tempCandidates, dict, 0, 0, oper);
      String oldTerm = terms[errorIndex];
      for (String t : tempCandidates.get(0)) {
        terms[errorIndex] = t;
        candidate.add(joinStrings( terms));
        terms[errorIndex] = oldTerm;
      }
    } else {
      for (int i = 0; i < terms.length; i++) {
        String tempQuery = terms[i];
        HashMap<Integer, HashSet<String>> tempCandidates = new HashMap<Integer, HashSet<String>>();
        tempCandidates.put(0, new HashSet<String>());
        move1AddCandidate(tempQuery, tempCandidates, dict, 0, 0, oper);
        String oldTerm = terms[i];
        for (String t : tempCandidates.get(0)) {
          terms[i] = t;
          candidate.add(joinStrings( terms));
          terms[i] = oldTerm;
        }
      }
    }
  }

  public void operateTwice(String query, ArrayList<Integer> parseResult, Dictionary dict, HashSet<String> candidate, int r, int s) {
    int errorNum = parseResult.get(0);
    String[] terms = query.split(" ", -1);
    if (errorNum == 2) {
      int errorIndex1 = parseResult.get(1);
      int errorIndex2 = parseResult.get(2);
      String tempQuery1 = terms[errorIndex1];
      String tempQuery2 = terms[errorIndex2];
      HashMap<Integer, HashSet<String>> tempCandidates1 = new HashMap<Integer, HashSet<String>>();
      tempCandidates1.put(0,  new HashSet<String>());
      HashMap<Integer, HashSet<String>> tempCandidates2 = new HashMap<Integer, HashSet<String>>();
      tempCandidates2.put(0,  new HashSet<String>());
      move1AddCandidate(tempQuery1, tempCandidates1, dict, 0, 0, r);
      move1AddCandidate(tempQuery2, tempCandidates2, dict, 0, 0, s);
      String oldTerm1 = terms[errorIndex1];
      String oldTerm2 = terms[errorIndex2];
      for (String t : tempCandidates1.get(0)) {
        for (String q : tempCandidates2.get(0)) {
          terms[errorIndex1] = t;
          terms[errorIndex2] = q;
          candidate.add(joinStrings( terms));
          terms[errorIndex1] = oldTerm1;
          terms[errorIndex2] = oldTerm2;
        }
      }
    } else if (errorNum == 1) {
      int errorIndex1 = parseResult.get(1);
      String tempQuery1 = terms[errorIndex1];
      HashMap<Integer, HashSet<String>> tempCandidates1 = new HashMap<Integer, HashSet<String>>();
      tempCandidates1.put(0, new HashSet<String>());
      tempCandidates1.put(1, new HashSet<String>());
      move1AddCandidate(tempQuery1, tempCandidates1, dict, 0, 1, r);
      for (int i = 0; i < terms.length; i++) {
        if (i == errorIndex1) {
          HashMap<Integer, HashSet<String>> tempCandidates11 = new HashMap<Integer, HashSet<String>>();
          tempCandidates11.put(0, new HashSet<String>());
          for (String t1 : tempCandidates1.get(0)) {
            move1AddCandidate(t1, tempCandidates11, dict, 0, 0, s);
          }
          for (String t1 : tempCandidates1.get(1)) {
            move1AddCandidate(t1, tempCandidates11, dict, 0, 0, s);
          }
          String oldTerm1 = terms[errorIndex1];
          for (String t : tempCandidates11.get(0)) {
            terms[errorIndex1] = t;
            String final_string = joinStrings( terms);
            if (!final_string.equals(query)) {
              candidate.add(final_string);
            }
            terms[errorIndex1] = oldTerm1;
          }
        } else {
          String tempQuery2 = terms[i];
          HashMap<Integer, HashSet<String>> tempCandidates2 = new HashMap<Integer, HashSet<String>>();
          tempCandidates2.put(0, new HashSet<String>());
          move1AddCandidate(tempQuery2, tempCandidates2, dict, 0, 0, s);
          String oldTerm1 = terms[errorIndex1];
          String oldTerm2 = terms[i];
          for (String t : tempCandidates1.get(0)) {
            for (String q : tempCandidates2.get(0)) {
              terms[errorIndex1] = t;
              terms[i] = q;
              candidate.add(joinStrings( terms));
              terms[errorIndex1] = oldTerm1;
              terms[i] = oldTerm2;
            }
          }
        }
      }
    } else {
      for (int i = 0; i < terms.length; i++) {
        for (int j = i; j < terms.length; j++) {
          if (i != j) {
            String tempQuery1 = terms[i];
            String tempQuery2 = terms[j];
            HashMap<Integer, HashSet<String>> tempCandidates1 = new HashMap<Integer, HashSet<String>>();
            tempCandidates1.put(0, new HashSet<String>());
            move1AddCandidate(tempQuery1, tempCandidates1, dict, 0, 0, r);
            HashMap<Integer, HashSet<String>> tempCandidates2 = new HashMap<Integer, HashSet<String>>();
            tempCandidates2.put(0, new HashSet<String>());
            move1AddCandidate(tempQuery2, tempCandidates2, dict, 0, 0, s);
            String oldTerm1 = terms[i];
            String oldTerm2 = terms[j];
            for (String t : tempCandidates1.get(0)) {
              for (String q : tempCandidates2.get(0)) {
                terms[i] = t;
                terms[j] = q;
                candidate.add(joinStrings( terms));
                terms[i] = oldTerm1;
                terms[j] = oldTerm2;
              }
            }
          } else {
            String tempQuery1 = terms[i];
            HashMap<Integer, HashSet<String>> tempCandidates1 = new HashMap<Integer, HashSet<String>>();
            tempCandidates1.put(0, new HashSet<String>());
            tempCandidates1.put(1, new HashSet<String>());
            move1AddCandidate(tempQuery1, tempCandidates1, dict, 0, 1, r);
            HashMap<Integer, HashSet<String>> tempCandidates11 = new HashMap<Integer, HashSet<String>>();
            tempCandidates11.put(0, new HashSet<String>());
            for (String t1 : tempCandidates1.get(0)) {
              move1AddCandidate(t1, tempCandidates11, dict, 0, 0, s);
            }
            for (String t1 : tempCandidates1.get(1)) {
              move1AddCandidate(t1, tempCandidates11, dict, 0, 0, s);
            }
            String oldTerm1 = terms[i];
            for (String t : tempCandidates11.get(0)) {
              terms[i] = t;
              String final_string = joinStrings( terms);
              if (!final_string.equals(query)) {
                candidate.add(final_string);
              }
              terms[i] = oldTerm1;
            }
          }
        }
      }
    }
  }

  HashSet<String> space1(String query, Dictionary dict, int oper) {
    HashSet<String> tempQueries = new HashSet<String>();
    switch(oper) {
      case 0:
        ArrayList<ArrayList<String>> queryDel1 = del1(query, dict, 1, true);
        for (String qp : queryDel1.get(0)) {
          tempQueries.add(qp);
        }
        break;
      case 1:
        ArrayList<ArrayList<String>> queryIns1 = ins1(query, dict, 1, true);
        for (String qp : queryIns1.get(0)) {
          tempQueries.add(qp);
        }
        break;
      case 2:
        ArrayList<ArrayList<String>> querySub1 = sub1(query, dict, 1, true);
        for (String qp : querySub1.get(0)) {
          tempQueries.add(qp);
        }
        break;
      case 3:
        ArrayList<ArrayList<String>> queryTrans1 = trans1(query, dict, 1, true);
        for (String qp : queryTrans1.get(0)) {
          tempQueries.add(qp);
        }
        break;
    }
    return tempQueries;
  }

  //Generate all candidates for the target query
  public Map<String, HashSet<String>> getCandidates(String query, LanguageModel languageModel,
      NoisyChannelModel nsm) throws Exception {
    Map<String, HashSet<String>> candidates = new HashMap<String, HashSet<String>>();
    Dictionary dict = languageModel.dict;
    candidates.put("0", new HashSet<String>());
    candidates.put("1a", new HashSet<String>());
    candidates.put("1b", new HashSet<String>());
    candidates.put("1c", new HashSet<String>());
    candidates.put("1d", new HashSet<String>());
    candidates.put("2aa", new HashSet<String>());
    candidates.put("2ab", new HashSet<String>());
    candidates.put("2ac", new HashSet<String>());
    candidates.put("2ad", new HashSet<String>());
    candidates.put("2bb", new HashSet<String>());
    candidates.put("2bc", new HashSet<String>());
    candidates.put("2bd", new HashSet<String>());
    candidates.put("2cc", new HashSet<String>());
    candidates.put("2cd", new HashSet<String>());
    candidates.put("2dd", new HashSet<String>());
    // no space operation
    ArrayList<Integer> parseResult = invalidCount(query, dict);
    if (parseResult.get(0) > 2) {
      return candidates;
    }
    if (parseResult.get(0) == 0) {
      candidates.get("0").add(query);
      for (int r = 0; r < 4; r++) {
        operateOnce(query, parseResult, dict, candidates.get("1" + (char)('a' + r)), r);
        for (int s = 0; s < 4; s++) {
          if (r < s) {
            operateTwice(query, parseResult, dict, candidates.get("2" + (char)('a' + r) + (char)('a' + s)), r, s);
          } else {
            operateTwice(query, parseResult, dict, candidates.get("2" + (char)('a' + s) + (char)('a' + r)), r, s);
          }
        }
      }
    }
    if (parseResult.get(0) == 1) {
      for (int r = 0; r < 4; r++) {
        operateOnce(query, parseResult, dict, candidates.get("1" + (char)('a' + r)), r);
        for (int s = 0; s < 4; s++) {
          if (r < s) {
            operateTwice(query, parseResult, dict, candidates.get("2" + (char)('a' + r) + (char)('a' + s)), r, s);
          } else {
            operateTwice(query, parseResult, dict, candidates.get("2" + (char)('a' + s) + (char)('a' + r)), r, s);
          }
        }
      }
    }
    if (parseResult.get(0) == 2) {
      for (int r = 0; r < 4; r++) {
        for (int s = 0; s < 4; s++) {
          if (r < s) {
            operateTwice(query, parseResult, dict, candidates.get("2" + (char)('a' + r) + (char)('a' + s)), r, s);
          } else {
            operateTwice(query, parseResult, dict, candidates.get("2" + (char)('a' + s) + (char)('a' + r)), r, s);
          }
        }
      }
    }

    // space operation once
    for (int r = 0; r < 4; r++) {
      HashSet<String> querySpace1s = space1(query, dict, r);
      for (String querySpace1 : querySpace1s) {
        ArrayList<Integer> parseResult2 = invalidCount(querySpace1, dict);
        if (parseResult2.get(0) == 0) {
          candidates.get("1" + (char)('a' + r)).add(querySpace1);
          for (int s = 0; s < 4; s++) {
            if (r < s) {
              operateOnce(querySpace1, parseResult2, dict, candidates.get("2" + (char)('a' + r) + (char)('a' + s)), s);
            } else {
              operateOnce(querySpace1, parseResult2, dict, candidates.get("2" + (char)('a' + s) + (char)('a' + r)), s);
            }
            HashSet<String> querySpace2s = space1(querySpace1, dict, s);
            for (String querySpace2 : querySpace2s) {
              ArrayList<Integer> parseResult3 = invalidCount(querySpace2, dict);
              if (parseResult3.get(0) == 0) {
                if (!querySpace2.equals(query)) {
                  if (r < s) {
                    candidates.get("2" + (char)('a' + r) + (char)('a' + s)).add(querySpace2);
                  } else {
                    candidates.get("2" + (char)('a' + s) + (char)('a' + r)).add(querySpace2);
                  }
                }
              }
            }
          }
        }
        if (parseResult2.get(0) == 1) {
          for (int s = 0; s < 4; s++) {
            if (r < s) {
              operateOnce(querySpace1, parseResult2, dict, candidates.get("2" + (char)('a' + r) + (char)('a' + s)), s);
            } else {
              operateOnce(querySpace1, parseResult2, dict, candidates.get("2" + (char)('a' + s) + (char)('a' + r)), s);
            }
            HashSet<String> querySpace2s = space1(querySpace1, dict, s);
            for (String querySpace2 : querySpace2s) {
              ArrayList<Integer> parseResult3 = invalidCount(querySpace2, dict);
              if (parseResult3.get(0) == 0) {
                if (!querySpace2.equals(query)) {
                  if (r < s) {
                    candidates.get("2" + (char)('a' + r) + (char)('a' + s)).add(querySpace2);
                  } else {
                    candidates.get("2" + (char)('a' + s) + (char)('a' + r)).add(querySpace2);
                  }
                }
              }
            }
          }
        }
      }
    }

    coun++;
    //System.err.println(coun);
    return candidates;
  }
  private String joinStrings(String[] strs){
    if (strs==null || strs.length == 0){return null;}
    StringBuilder sb = new StringBuilder();
    for(String str: strs){
      sb.append(str+" ");
    }
    sb.setLength(sb.length()-1);
    return sb.toString();
  }
}