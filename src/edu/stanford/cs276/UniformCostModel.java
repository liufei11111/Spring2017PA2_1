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
//    int dis = editDistDP(original,R);
    int distance = (int)(dis.charAt(0)-'0');
//    String[] origStrs = original.split(" ");
//    String[] rStrs = R.split(" ");
//    if (origStrs.length<rStrs.length){
//      distance+=1;
//    }
//    // handle past/current tense conversion
//    if (origStrs.length == rStrs.length) {
//      int len = origStrs.length;
//      for (int i = 0; i < len; ++i) {
//        String origStr = origStrs[i];
//        int origLen = origStrs[i].length();
//        String rStr = rStrs[i];
//        int rLen = rStrs[i].length();
//        if (rLen > origLen) {
//          int temp = rLen;
//          String tempStr = rStr;
//          rLen = origLen;
//          rStr = origStr;
//          origStr = tempStr;
//          origLen = temp;
//
//        }

//        if (origLen - rLen <= 2 && origStr.length() >= 4 && origStr.substring(origLen - 2)
//            .equals("ed") && rLen >= 3 && rStr.charAt(rLen - 1) == 's') {
////            System.out.println("Original: "+original+", R: "+R+", distance: "+distance);
//          distance += 2;
//
//        } else if (origStr.length() > 2 && origStr.charAt(origLen - 1) == 's' && origStr
//            .substring(0, origLen - 1).equals(rStr)) {
//          distance += 2;
//        }
//
//      }
//    }
//
//
//
//    }else{
//      distance+=1;
//    }
      if (distance >=1){
        return  Math.log(Config.singleEditProb)*distance;//-4.605170185988091*distance;//precomputed for
      }else{
        return Math.log(1-Config.singleEditProb);//-0.01005033585350145;
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
