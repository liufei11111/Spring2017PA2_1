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
  public Map<String, Integer> del;
  public Map<String, Integer> ins;
  public Map<String, Integer> sub;
  public Map<String, Integer> trans;
  public Map<String, Integer> occurence;
  public Map<String, Integer> occurence2;

  private String getDelIndex(String noisy, String clean) {
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
    String tuple = pre + "" + cur;
    return tuple;
  }

  private String getInsIndex(String noisy, String clean) {
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
    String tuple = pre + "" + cur;
    return tuple;
  }

  private String getSubIndex(String noisy, String clean) {
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
    String tuple = pre + "" + cur + "" + (char)appear;
    return tuple;
  }


  public EmpiricalCostModel(String editsFile) throws IOException {
    del = new HashMap<String, Integer>();
    ins = new HashMap<String, Integer>();
    sub = new HashMap<String, Integer>();
    trans = new HashMap<String, Integer>();
    occurence = new HashMap<String, Integer>();
    occurence2 = new HashMap<String, Integer>();
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
        String fake_tuple = c + "";
        if (occurence.containsKey(fake_tuple)) {
          occurence.put(fake_tuple, occurence.get(fake_tuple) + 1);
        } else {
          occurence.put(fake_tuple, 1);
        }
        if (i != clean.length() - 1) {
          char c2 = clean.charAt(i + 1);
          String tuple = c + "" + c2;
          if (occurence2.containsKey(tuple)) {
            occurence2.put(tuple, occurence2.get(tuple) + 1);
          } else {
            occurence2.put(tuple, 1);
          }
        }
      }
      if (noisy.length() == clean.length()) {
        String tuple = getSubIndex(noisy, clean);
        int appear = (int)tuple.charAt(2);
        tuple = tuple.substring(0, 2);
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
        String tuple = getInsIndex(noisy, clean);
        if (ins.containsKey(tuple)) {
          ins.put(tuple, ins.get(tuple) + 1);
        } else {
          ins.put(tuple, 1);
        }
      } else if (noisy.length() == clean.length() - 1) {
        String tuple = getDelIndex(noisy, clean);
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
  public double editProbability(String query, String cand, String dis) {
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
        } else if (op1 =='d' && op2 == 'd') {
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
      String tuple = pre[i] + "" + cur[i];
      String fake_tuple0 = pre[i] + "";
      String fake_tuple1 = cur[i] + "";

      int qwe = 0;
      int rty = 0;
      switch(sym[i]) {
        case 'a':
          qwe = 0;
          if (ins.containsKey(tuple)) {
            qwe = ins.get(tuple);
          }
          rty = 0;
         if (occurence.containsKey(fake_tuple0)) {
           rty = occurence.get(fake_tuple0);
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
          if (occurence.containsKey(fake_tuple1)) {
            rty = occurence.get(fake_tuple1);
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
