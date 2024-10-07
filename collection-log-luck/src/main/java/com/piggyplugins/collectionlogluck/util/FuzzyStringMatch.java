package com.piggyplugins.collectionlogluck.util;

public class FuzzyStringMatch {

    // given an input string and a candidate string, return a fuzzy match score using a token-order-invariant
    // Levenshtein edit distance
    public static double fuzzyMatchScore(String input, String candidate) {
        String[] inputTokens = input.trim().split("\\s+");
        String[] candidateTokens = candidate.split("\\s+");

        int tokenWiseBestScoreSum = 0;

        for (int i = 0; i < inputTokens.length; i++) {
            String token = cleanString(inputTokens[i]);
            String tokenWithoutPunctuation = stripPunctuation(token);

            int bestTokenPairingScore = Integer.MIN_VALUE;

            for (int j = 0; j < candidateTokens.length; j++) {
                String candidateToken = cleanString(candidateTokens[j]);
                String candidateTokenWithoutPunctuation = stripPunctuation(candidateToken);

                int tokenPairScore = -FuzzyStringMatch.getLevenshteinEditDistance(token, candidateToken);
                int tokenPairScoreWithoutPunctuation = -FuzzyStringMatch.getLevenshteinEditDistance(
                        tokenWithoutPunctuation, candidateTokenWithoutPunctuation);
                int optimisticTokenPairScore = Math.max(tokenPairScore, tokenPairScoreWithoutPunctuation);

                if (optimisticTokenPairScore > bestTokenPairingScore) {
                    bestTokenPairingScore = optimisticTokenPairScore;
                }
            }

            tokenWiseBestScoreSum += bestTokenPairingScore;
        }

        // Also get an overall word distance score
        String inputWithoutPunctuation = cleanString(input.trim());
        String candidateWithoutPunctuation = stripPunctuation(candidate);
        int editDistance = -FuzzyStringMatch.getLevenshteinEditDistance(input, candidate);
        int editDistanceWithoutPunctuation = -FuzzyStringMatch.getLevenshteinEditDistance(
                inputWithoutPunctuation, candidateWithoutPunctuation);
        int overallEditDistance = Math.max(editDistance, editDistanceWithoutPunctuation);

        // There should still be a slight penalty to overall edit distance if tokens are out of order or entirely missing
        return tokenWiseBestScoreSum + overallEditDistance / 5.0;
    }

    static int getLongestCommonSubstringLength(String s1, String s2) {
        if (s1.equals(s2)) {
            return s1.length();
        }
        int dp[][] = new int[s1.length() + 1][s2.length() + 1];

        int result = 0;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    result = Math.max(result, dp[i][j]);
                }
            }
        }
        return result;
    }

    static int getLevenshteinEditDistance(String s1, String s2) {
        if (s1.equals(s2)) {
            return 0;
        }
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                    dp[i][j] = Math.min(
                            dp[i - 1][j - 1] + cost,
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    private static String cleanString(String input) {
        return input.trim().toLowerCase();
    }

    private static String stripPunctuation(String input) {
        return input.replaceAll("[^\\w\\s]", "");
    }

}
