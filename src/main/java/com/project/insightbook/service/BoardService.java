package com.project.insightbook.service;

import com.project.insightbook.dto.BoardDto;
import com.project.insightbook.dto.BoardFileDto;
import com.project.insightbook.paging.CommonParams;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

public interface BoardService {

    Map<String,Object> selectBoardList(CommonParams params) throws Exception;
    void insertBoard(BoardDto boardDto, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception;
    BoardDto selectBoardDetail(int boardIdx) throws Exception;
    void updateHitCount(int boardIdx) throws Exception;
    void updateBoard(BoardDto boardDto) throws Exception;
    void deleteBoard(int boardIdx) throws Exception;
    BoardFileDto selectBoardFileInformation(int idx, int boardIdx) throws Exception;
}
