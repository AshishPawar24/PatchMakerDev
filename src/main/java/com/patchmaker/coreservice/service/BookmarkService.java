package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.response.BookmarkResponse;

import java.util.List;

public interface BookmarkService {
    BookmarkResponse addBookmark(Long projectId);
    void removeBookmark(Long projectId);
    List<BookmarkResponse> getMyBookmarks();
}