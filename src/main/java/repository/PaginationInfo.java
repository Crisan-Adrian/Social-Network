package repository;

import java.util.Map;

public class PaginationInfo {
    private int pageSize;
    private int pageNumber;
    private Map<String, String> matcher;

    public PaginationInfo() {
        pageSize = 1;
        pageNumber = 0;
        matcher = null;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Map<String, String> getMatcher() {
        return matcher;
    }

    public void setMatcher(Map<String, String> matcher) {
        this.matcher = matcher;
    }
}
