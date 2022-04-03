package com.project.insightbook.service;

import com.project.insightbook.common.FileUtils;
import com.project.insightbook.dto.BoardDto;
import com.project.insightbook.dto.BoardFileDto;
import com.project.insightbook.mapper.BoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Iterator;
import java.util.List;



@Service
@Transactional
@Slf4j
public class BoardServiceImpl implements BoardService{

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public List<BoardDto> selectBoardList() throws Exception{
        return boardMapper.selectBoardList();
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
