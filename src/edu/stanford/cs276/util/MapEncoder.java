package edu.stanford.cs276.util;

import static edu.stanford.cs276.Config.wordThreshold;

import edu.stanford.cs276.Config;
import edu.stanford.cs276.LanguageModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by feiliu on 5/4/17.
 */
public class MapEncoder {
  public void saveMapStringInteger(Map<String,Integer> dict, String fileName)
      throws IOException {
    FileWriter fw = new FileWriter(fileName);
    int count = 0;
    for (Entry<String, Integer> entry:dict.entrySet()){
      fw.write(entry.getValue()+"@");
      fw.write(entry.getKey());
      if (count!=dict.size()-1){
        count++;
        fw.write("\n");
      }
    }
    fw.close();
  }

  public Map<String,Integer> retrieveMapStringInteger(String fileName)
      throws IOException {
    Map<String,Integer> map = new HashMap<>();
    BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
    String line = null;
    while((line = br.readLine())!=null){
      String[] items = line.split("@");
      String key = items[1];
      int num = Integer.parseInt(items[0]);
      map.put(key,num);
    }
    br.close();
    return map;
  }

  public void saveUnigram( Map<String,Integer> dict)
      throws IOException {
    saveMapStringInteger(dict, Config.unigramFile);
  }

  public  Map<String,Integer> retrieveUnigram(LanguageModel lm)
      throws IOException {
    Map<String,Integer> map = new HashMap<>();
    BufferedReader br = new BufferedReader(new FileReader(new File(Config.unigramFile)));
    String line = null;
    while((line = br.readLine())!=null){
      String[] items = line.split("@");
      String key = items[1];
      int num = Integer.parseInt(items[0]);
      map.put(key,num);
      lm.dict.termCount+=num;
    }
    br.close();
    return map;
  }
  public void saveBigram( Map<Pair<String,String>,Integer> dict)
      throws IOException {
//    RandomAccessFile rafUnigram = new RandomAccessFile(Config.unigramFile,"rw");
    FileWriter fw = new FileWriter(Config.bigramFile);
    int count = 0;
    for (Entry<Pair<String,String>, Integer> entry:dict.entrySet()){
      fw.write(entry.getValue()+"@");
      fw.write(entry.getKey().getFirst()+"@");
      fw.write(entry.getKey().getSecond());
      if (count!=dict.size()-1){
        count++;
        fw.write("\n");
      }
    }
    fw.close();
  }
  public  Map<Pair<String,String>,Integer> retrieveBigram()
      throws IOException {
    Map<Pair<String,String>,Integer> map = new HashMap<>();
    BufferedReader br = new BufferedReader(new FileReader(new File(Config.bigramFile)));
    String line = null;
    while((line = br.readLine())!=null){
      String[] items = line.split("@");
      map.put(new Pair(items[1],items[2]),Integer.parseInt(items[0]));
    }
    return map;
  }
}
