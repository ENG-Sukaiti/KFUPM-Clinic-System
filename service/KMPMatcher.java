package kfupm.clinic.service;

public class KMPMatcher implements StringMatcher {
    
    @Override
    public boolean contains(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        if (text == null) return false;

        String t = text.toLowerCase();
        String p = pattern.toLowerCase();

        int n = t.length();
        int m = p.length();
        int[] lps = computeLPSArray(p);

        int i = 0;
        int j = 0;

        while (i < n) {
            if (p.charAt(j) == t.charAt(i)) {
                j++;
                i++;
            }
            if (j == m) {
                return true;
            } else if (i < n && p.charAt(j) != t.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    private int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
}r
}
