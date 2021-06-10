package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PagingUtils {
    public static boolean validateMatchers(Map<String, String> matchParams, List<String> validMatchKeys) {
        List<String> keys = new ArrayList<>();
        for (String key : matchParams.keySet()) {
            if (keys.contains(key)) {
                return false;
            }
            if (!validMatchKeys.contains(key)) {
                return false;
            }
            keys.add(key);
        }
        return true;
    }

    public static boolean validatePage(int pageSize, int pageNumber) {
        return pageSize >= 1 && pageNumber >= 0;
    }

    public static String buildMatcher(Set<String> keys) {
        if (keys.isEmpty()) {
            return "";
        }
        StringBuilder matcher = new StringBuilder("WHERE ");
        boolean first = true;
        for (String key : keys) {
            if (!first) {
                matcher.append("AND ");
            }
            matcher.append(key).append(" LIKE ? ");
            first = false;
        }
        return matcher.toString();
    }
}
