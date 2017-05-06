package edu.stanford.cs276;

public class Config {
  public static final String noisyChannelFile = "noisyChannel";
  public static final String languageModelFile = "languageModel";
  public static final String candidateGenFile = "candidateGenerator";
  public static final String unigramFile = "unigram.txt";
  public static final String bigramFile = "bigram.txt";
  public static double languageModelScalingFactor = 0.99;



  public static double eps = 1e-256;
  public static int wordThreshold=25;
  public static double singleEditProb = 0.01;// 145 .. from 0.005 - 0.1 roughtly the same
  public static double smoothingFactor=0.1;// tested not significant 146 ..
}
