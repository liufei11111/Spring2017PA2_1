package edu.stanford.cs276;

/**
 * Implement {@link EditCostModel} interface by assuming assuming
 * that any single edit in the Damerau-Levenshtein distance is equally likely,
 * i.e., having the same probability
 */
public class UniformCostModel implements EditCostModel {
	
	private static final long serialVersionUID = 1L;
	
  @Override
  public double editProbability(String original, String R, String dis) {

    int distance = (int)(dis.charAt(0)-'0');
      if (distance >=1){
        return -4.605170185988091*distance;// Math.log(Config.singleEditProb)*distance;
      }else{
        return -0.01005033585350145;//Math.log(1-Config.singleEditProb);
      }
  }
}
