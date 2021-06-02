package repository;

import java.util.Dictionary;

public class PaginationInfo {
    private int pageSize;
    private int pageNumber;
    private Dictionary<String, Object> matcher;

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

    public Dictionary<String, Object> getMatcher() {
        return matcher;
    }

    public void setMatcher(Dictionary<String, Object> matcher) {
        this.matcher = matcher;
    }
}
