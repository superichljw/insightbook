package com.project.insightbook.mapper;

import com.project.insightbook.dto.BoardDto;
import com.project.insightbook.dto.BoardFileDto;
import com.project.insightbook.paging.CommonParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
    List<BoardDto> selectBoardList(CommonParams params) throws Exception;
    void insertBoard(BoardDto boardDto) throws Exception;
    BoardDto selectBoardDetail(int boardIdx) throws NullPointerException;
    void updateHitCount(int boardIdx) throws NullPointerException;
    void updateBoard(BoardDto boardDto) throws Exception;
    void deleteBoard(int boardIdx) throws Exception;
    void insertBoardFileList(List<BoardFileDto> list) throws Exception;
    List<BoardFileDto> selectBoardFileList(int boardIdx) throws Exception;
    int count(CommonParams params) throws Exception;
    BoardFileDto selectBoardFileInformation(@Param("idx") int idx, @Param("boardIdx") int boardIdx) throws Exception;
}
