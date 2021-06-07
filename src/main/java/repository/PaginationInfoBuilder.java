package repository;

import java.util.Map;

public class PaginationInfoBuilder {
    private PaginationInfo paginationInfo;

    public PaginationInfoBuilder create() {
        paginationInfo = new PaginationInfo();
        return this;
    }
    public PaginationInfoBuilder setPageSize(int pageSize){
        paginationInfo.setPageSize(pageSize);
        return this;
    }

    public PaginationInfoBuilder setPageNumber(int pageNumber) {
        paginationInfo.setPageNumber(pageNumber);
        return this;
    }

    public PaginationInfoBuilder setMatcher(Map<String, String> matcher) {
        paginationInfo.setMatcher(matcher);
        return this;
    }

    public PaginationInfo build() {
        return paginationInfo;
    }
}
