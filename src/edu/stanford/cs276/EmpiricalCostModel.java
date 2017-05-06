package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Implement {@link EditCostModel} interface. Use the query corpus to learn a model
 * of errors that occur in our dataset of queries, and use this to compute P(R|Q).
 */
public class EmpiricalCostModel implements EditCostModel {
  private static final long serialVersionUID = 1L;
  private static Map<ArrayList<Character>, Integer> del = new HashMap<ArrayList<Character>, Integer>();
  private static Map<ArrayList<Character>, Integer> ins = new HashMap<ArrayList<Character>, Integer>();
  private static Map<ArrayList<Character>, Integer> sub = new HashMap<ArrayList<Character>, Integer>();
  private static Map<ArrayList<Character>, Integer> trans = new HashMap<ArrayList<Character>, Integer>();
  private static Map<Character, Integer> occurence = new HashMap<Character, Integer>();
  private static Map<ArrayList<Character>, Integer> occurence2 = new HashMap<ArrayList<Character>, Integer>();

  private ArrayList<Character> getDelIndex(String noisy, String clean) {
    int index1 = 0;
    int index2 = 0;
    while(index1 < noisy.length() && index2 < clean.length()) {
      if (noisy.charAt(index1) == clean.charAt(index2)) {
        index1++;
        index2++;
      } else {
        break;
      }
    }
    char pre;
    char cur;
    if (index1 == 0) {
      pre = (char)0;
    } else {
      pre = noisy.charAt(index1 - 1);
    }
    cur = clean.charAt(index2);
    ArrayList<Character> tuple = new ArrayList<Character>();
    tuple.add(pre);
    tuple.add(cur);
    return tuple;
  }

  private ArrayList<Character> getInsIndex(String noisy, String clean) {
    int index1 = 0;
    int index2 = 0;
    while(index1 < noisy.length() && index2 < clean.length()) {
      if (noisy.charAt(index1) == clean.charAt(index2)) {
        index1++;
        index2++;
      } else {
        break;
      }
    }
    char pre;
    char cur;
    if (index2 == 0) {
      pre = (char)(0);
    } else {
      pre = clean.charAt(index2 - 1);
    }
    cur = noisy.charAt(index1);
    ArrayList<Character> tuple = new ArrayList<Character>();
    tuple.add(pre);
    tuple.add(cur);
    return tuple;
  }

  private ArrayList<Character> getSubIndex(String noisy, String clean) {
    int index1 = 0;
    int index2 = 0;
    char pre = (char)0;
    char cur = (char)0;
    int appear = 0;
    while(index1 < noisy.length() && index2 < clean.length()) {
      if (noisy.charAt(index1) == clean.charAt(index2)) {
        index1++;
        index2++;
      } else {
        if (appear == 0) {
          pre = noisy.charAt(index1);
          cur = clean.charAt(index2);
        }
        appear++;
        index1++;
        index2++;
        if (appear == 2) {
          break;
        }
      }
    }
    ArrayList<Character> tuple = new ArrayList<Character>();
    tuple.add(pre);
    tuple.add(cur);
    tuple.add((char)appear);
    return tuple;
  }


  public EmpiricalCostModel(String editsFile) throws IOException {
    BufferedReader input = new BufferedReader(new FileReader(editsFile));
    System.out.println("Constructing edit distance map...");
    String line = null;
    while ((line = input.readLine()) != null) {
      Scanner lineSc = new Scanner(line);
      lineSc.useDelimiter("\t");
      String noisy = lineSc.next();
      String clean = lineSc.next();
      lineSc.close();
      for (int i = -1; i < clean.length(); i++) {
        char c = (char)(0);
        if (i >= 0) {
          c = clean.charAt(i);
        }
        if (occurence.containsKey(c)) {
          occurence.put(c, occurence.get(c) + 1);
        } else {
          occurence.put(c, 1);
        }
        if (i != clean.length() - 1) {
          char c2 = clean.charAt(i + 1);
          ArrayList<Character> tuple = new ArrayList<Character>();
          tuple.add(c);
          tuple.add(c2);
          if (occurence2.containsKey(tuple)) {
            occurence2.put(tuple, occurence2.get(tuple) + 1);
          } else {
            occurence2.put(tuple, 1);
          }
        }
      }
      if (noisy.length() == clean.length()) {
        ArrayList<Character> tuple = getSubIndex(noisy, clean);
        int appear = tuple.get(2);
        tuple.remove(2);
        if (appear == 1) {
          if (sub.containsKey(tuple)) {
            sub.put(tuple, sub.get(tuple) + 1);
          } else {
            sub.put(tuple, 1);
          }
        } else if (appear == 2) {
          if (trans.containsKey(tuple)) {
            trans.put(tuple, trans.get(tuple) + 1);
          } else {
            trans.put(tuple, 1);
          }
        }
      } else if (noisy.length() == clean.length() + 1) {
        ArrayList<Character> tuple = getInsIndex(noisy, clean);
        if (ins.containsKey(tuple)) {
          ins.put(tuple, ins.get(tuple) + 1);
        } else {
          ins.put(tuple, 1);
        }
      } else if (noisy.length() == clean.length() - 1) {
        ArrayList<Character> tuple = getDelIndex(noisy, clean);
        if (del.containsKey(tuple)) {
          del.put(tuple, del.get(tuple) + 1);
        } else {
          del.put(tuple, 1);
        }
      } else {
        System.err.println("Unexpected1:" + noisy + " " + clean);
      }
    }
    input.close();
    System.out.println("Done.");
  }

  // You need to add code for this interface method to calculate the proper empirical cost.
  @Override
  public double editProbability(String cand, String query, String dis) {
    int distance = dis.charAt(0) - '0';
    if (distance == 0) {
      return 0.95;
    }
    char op1;
    char op2;
    if (distance == 1) {
      op1 = dis.charAt(1);
      op2 = dis.charAt(1);
    } else {
      op1 = dis.charAt(1);
      op2 = dis.charAt(2);
    }
    char[] R = query.toCharArray();
    char[] original = cand.toCharArray();
    int index_r = 0;
    int index_o = 0;
    int rL = R.length;
    int oL = original.length;
    char[] sym = new char[2];
    char[] pre = new char[2];
    char[] cur = new char[2];
    int processed_op = 0;
    boolean success = false;
    while(index_r < rL || index_o < oL) {
      if (index_r < rL && index_o < oL && R[index_r] == original[index_o]) {
        index_r++;
        index_o++;
      } else {
        if (processed_op == distance) {
          break;
        }
        char op;
        if (processed_op == 0) {
          op = op1;
        } else {
          op = op2;
        }
        boolean terminate = false;
        switch (op) {
          case 'a':
            if (index_o == 0) {
              pre[processed_op] = (char)(0);
            } else {
              pre[processed_op] = original[index_o - 1];
            }
            cur[processed_op] = R[index_r];
            index_r++;
            break;
          case 'b':
            if (index_r == 0) {
              pre[processed_op] = (char)0;
            } else {
              pre[processed_op] = R[index_r - 1];
            }
            cur[processed_op] = original[index_o];
            index_o++;
            break;
          case 'c':
            if (index_r == rL || index_o == oL) {
              terminate = true;
              break;
            }
            pre[processed_op] = R[index_r];
            cur[processed_op] = original[index_o];
            index_o++;
            index_r++;
            break;
          case 'd':
            if (index_o + 1 >= oL) {
              terminate = true;
              break;
            }
            pre[processed_op] = original[index_o + 1];
            cur[processed_op] = original[index_o];
            if (index_r < rL - 1 && index_o < oL - 1) {
              char og = original[index_o];
              original[index_o] = original[index_o + 1];
              original[index_o + 1] = og;
            } else {
              terminate = true;
            }
            break;
        }
        if (terminate) {
          break;
        }
        sym[processed_op] = op;
        processed_op++;
      }
    }
    if (index_r == rL && index_o == oL && processed_op <= distance) {
      success = true;
    }
    if (!success && distance == 2) {
      char temp = op1;
      op1 = op2;
      op2 = temp;
      original = cand.toCharArray();
      index_r = 0;
      index_o = 0;
      processed_op = 0;
      while(index_r < rL || index_o < oL) {
        if (index_r < rL && index_o < oL && R[index_r] == original[index_o]) {
          index_r++;
          index_o++;
        } else {
          if (processed_op == distance) {
            break;
          }
          char op;
          if (processed_op == 0) {
            op = op1;
          } else {
            op = op2;
          }
          boolean terminate = false;
          switch (op) {
            case 'a':
              if (index_o == 0) {
                pre[processed_op] = (char)(0);
              } else {
                pre[processed_op] = original[index_o - 1];
              }
              cur[processed_op] = R[index_r];
              index_r++;
              break;
            case 'b':
              if (index_r == 0) {
                pre[processed_op] = (char)0;
              } else {
                pre[processed_op] = R[index_r - 1];
              }
              cur[processed_op] = original[index_o];
              index_o++;
              break;
            case 'c':
              if (index_r == rL || index_o == oL) {
                terminate = true;
                break;
              }
              pre[processed_op] = R[index_r];
              cur[processed_op] = original[index_o];
              index_o++;
              index_r++;
              break;
            case 'd':
              if (index_o + 1 >= oL) {
                terminate = true;
                break;
              }
              pre[processed_op] = original[index_o + 1];
              cur[processed_op] = original[index_o];
              if (index_r < rL - 1 && index_o < oL - 1) {
                char og = original[index_o];
                original[index_o] = original[index_o + 1];
                original[index_o + 1] = og;
              } else {
                terminate = true;
              }
              break;
          }
          if (terminate) {
            break;
          }
          sym[processed_op] = op;
          processed_op++;
        }
      }
      if (index_r == rL && index_o == oL && processed_op <= distance) {
        success = true;
      }
    }
    if (!success) {
      if (distance != 2) {
        System.err.println("impossbile!");
      } else {
        if (op1 == 'b' && op2 == 'd') {
        } else if (op1 =='d' && op2 == 'b') {
        } else {
          System.err.println("impossible v2!");
        }
      }
      // I have verfied that every case is indeed impossbile!
      // e.g. orf -> for, irn-> ni.
      return 0.0;
    }

    double prob = 1.0;
    int term_size = occurence.size();
    int term_tuple_size = (term_size - 1) * term_size;
    for (int i = 0; i < processed_op; i++) {
      ArrayList<Character> tuple = new ArrayList<Character>();
      tuple.add(pre[i]);
      tuple.add(cur[i]);
      int qwe = 0;
      int rty = 0;
      switch(sym[i]) {
        case 'a':
          qwe = 0;
          if (ins.containsKey(tuple)) {
            qwe = ins.get(tuple);
          }
           rty = 0;
         if (occurence.containsKey(tuple.get(0))) {
           rty = occurence.get(tuple.get(0));
         }
         prob = prob * (double)(qwe + 1) / (rty + term_size);
         break;
        case 'b':
          qwe = 0;
          if (del.containsKey(tuple)) {
            qwe = del.get(tuple);
          }
          rty = 0;
          if (occurence2.containsKey(tuple)) {
            rty = occurence2.get(tuple);
          }
          prob = prob * (double)(qwe + 1) / (rty + term_tuple_size);
          break;
        case 'c':
          qwe = 0;
          if (sub.containsKey(tuple)) {
            qwe = sub.get(tuple);
          }
          rty = 0;
          if (occurence.containsKey(tuple.get(1))) {
            rty = occurence.get(tuple.get(1));
          }
          prob = prob * (double)(qwe + 1) / (rty + term_size);
          break;
        case 'd':
          qwe = 0;
          if (trans.containsKey(tuple)) {
            qwe = trans.get(tuple);
          }
          rty = 0;
          if (occurence2.containsKey(tuple)) {
            rty = occurence2.get(tuple);
          }
          prob = prob * (double)(qwe + 1) / (rty + term_tuple_size);
          break;
      }
    }
    return prob;
  }
}
