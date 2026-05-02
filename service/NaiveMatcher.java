package kfupm.clinic.service;

public class NaiveMatcher implements StringMatcher {
    @Override
    public boolean contains(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        if (text == null) return false;
        
        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        
        int n = t.length();
        int m = p.length();
        
        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (t.charAt(i + j) != p.charAt(j)) {
                    break;
                }
            }
            if (j == m) return true;
        }
        return false;
    }
}
