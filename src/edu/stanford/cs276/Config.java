package edu.stanford.cs276;

public class Config {
  public static final String noisyChannelFile = "noisyChannel";
  public static final String languageModelFile = "languageModel";
  public static final String candidateGenFile = "candidateGenerator";
  public static final String unigramFile = "unigram.txt";
  public static final String bigramFile = "bigram.txt";
  public static final String delFile = "del.txt";
  public static final String insFile = "ins.txt";
  public static final String subFile = "sub.txt";
  public static final String transFile = "trans.txt";
  public static final String occurenceFile = "occurence.txt";
  public static final String occurence2File = "occurence2.txt";
  // tuned
  public static double languageModelScalingFactor = 0.4;// empirical value for language model scaling
  public static double smoothingFactor=0.1;// empirical value for smoothing factor for unigram/bigram smoothing
  // assumed/ no need to tune
  public static double eps = 1e-300;
  public static int wordThreshold=25;
  public static double singleEditProb = 0.01;

}
