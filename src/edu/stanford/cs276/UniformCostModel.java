package edu.stanford.cs276;

/**
 * Implement {@link EditCostModel} interface by assuming assuming
 * that any single edit in the Damerau-Levenshtein distance is equally likely,
 * i.e., having the same probability
 */
public class UniformCostModel implements EditCostModel {
	
	private static final long serialVersionUID = 1L;
	
  @Override
  public double editProbability(String original, String R, int distance) {
//    int dis = editDistDP(original,R);

    if (distance == 0){
      return Math.log(1-Config.singleEditProb);
    }else{
      double combinorial = 0.0;
      double n = original.length();
      for (int k=distance;k>=1;--k){
        combinorial += Math.log(n-k+1.0)-Math.log(k);
        combinorial += k*Math.log(Config.singleEditProb)+(n-k)*Math.log(1-Config.singleEditProb);
      }
      return Math.log(Config.singleEditProb)+combinorial;
    }

  }
  static int editDistDP(String str1, String str2){
    int m = str1.length();
    int n = str2.length();
    // Create a table to store results of subproblems
    int dp[][] = new int[m+1][n+1];

    // Fill d[][] in bottom up manner
    for (int i=0; i<=m; i++)
    {
      for (int j=0; j<=n; j++)
      {
        // If first string is empty, only option is to
        // isnert all characters of second string
        if (i==0)
          dp[i][j] = j;  // Min. operations = j

          // If second string is empty, only option is to
          // remove all characters of second string
        else if (j==0)
          dp[i][j] = i; // Min. operations = i

          // If last characters are same, ignore last char
          // and recur for remaining string
        else if (str1.charAt(i-1) == str2.charAt(j-1))
          dp[i][j] = dp[i-1][j-1];

          // If last character are different, consider all
          // possibilities and find minimum
        else
          dp[i][j] = 1 + Math.min(dp[i][j-1], Math.min( // Insert
              dp[i-1][j],  // Remove
              dp[i-1][j-1])); // Replace
      }
    }

    return dp[m][n];
  }
}
