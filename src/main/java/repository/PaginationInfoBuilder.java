package repository;

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

    public PaginationInfoBuilder setMatcher(String matcher) {
        paginationInfo.setMatcher(matcher);
        return this;
    }

    public PaginationInfo build() {
        return paginationInfo;
    }
}
