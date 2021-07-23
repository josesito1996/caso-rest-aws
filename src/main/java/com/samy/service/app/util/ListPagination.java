package com.samy.service.app.util;

import java.util.Collections;
import java.util.List;

public class ListPagination<T> {

    /**
     * returns a view (not a new list) of the sourceList for the range based on page
     * and pageSize
     * 
     * @param sourceList
     * @param page,      page number should start from 1
     * @param pageSize
     * @return custom error can be given instead of returning emptyList
     */
    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        if (pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if (sourceList == null || sourceList.size() <= fromIndex) {
            return Collections.emptyList();
        }

        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

}
