package com.project.insightbook.service;

import com.project.insightbook.common.FileUtils;
import com.project.insightbook.dto.BoardDto;
import com.project.insightbook.dto.BoardFileDto;
import com.project.insightbook.mapper.BoardMapper;
import com.project.insightbook.paging.CommonParams;
import com.project.insightbook.paging.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;


@Service
@Transactional
@Slf4j
public class BoardServiceImpl implements BoardService{

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private FileUtils fileUtils;

//    @Override
//    public List<BoardDto> selectBoardList() throws Exception{
//        return boardMapper.selectBoardList();
//    }
    @Override
    public Map<String,Object> selectBoardList(CommonParams params) throws Exception{
        // 게시글 수 조회
        int count = boardMapper.count(params);

        // 등록된 게시글이 없는 경우, 로직 종료
        if (count < 1) {
            return Collections.emptyMap();
        }

        // 페이지네이션 정보 계산
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        // 게시글 리스트 조회
        List<BoardDto> list = boardMapper.selectBoardList(params);

        // 데이터 반환
        Map<String, Object> response = new HashMap<>();
        response.put("params", params);
        response.put("list", list);
        return response;
    }

    @Override
    public void insertBoard(BoardDto boardDto, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
        boardMapper.insertBoard(boardDto);
        List<BoardFileDto> list = fileUtils.parseFileInfo(boardDto.getBoardIdx(), multipartHttpServletRequest);
//        boardMapper.insertBoardFileList(list);
        if(!CollectionUtils.isEmpty(list)){
            boardMapper.insertBoardFileList(list);
        }

//      파일 정보 로그찍기용
//        if(ObjectUtils.isEmpty(multipartHttpServletRequest) == false){
//            Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
//            String name;
//            while(iterator.hasNext()){
//                name = iterator.next();
//                log.debug("file tag name : " + name);
//                List<MultipartFile> list = multipartHttpServletRequest.getFiles(name);
//                for(MultipartFile multipartFile : list){
//                    log.debug("start file information");
//                    log.debug("file name : " + multipartFile.getName());
//                    log.debug("file size : " + multipartFile.getSize());
//                    log.debug("file content type : " + multipartFile.getContentType());
//                    log.debug("end file information");
//                }
//            }
//        }
    }

    @Override
    public BoardDto selectBoardDetail(int boardIdx) throws Exception{
//        int i= 10/0;
        BoardDto board = boardMapper.selectBoardDetail(boardIdx);
        List<BoardFileDto> fileList = boardMapper.selectBoardFileList(boardIdx);
        board.setFileList(fileList);
        updateHitCount(boardIdx);

        return board;
    }

    @Override
    public void updateHitCount(int boardIdx) throws Exception{
        boardMapper.updateHitCount(boardIdx);
    }

    @Override
    public void updateBoard(BoardDto boardDto) throws Exception{
        boardMapper.updateBoard(boardDto);
    }

    @Override
    public void deleteBoard(int boardIdx) throws Exception{
        boardMapper.deleteBoard(boardIdx);
    }

    @Override
    public BoardFileDto selectBoardFileInformation(int idx, int boardIdx) throws Exception{
        return boardMapper.selectBoardFileInformation(idx, boardIdx);
    }

}
