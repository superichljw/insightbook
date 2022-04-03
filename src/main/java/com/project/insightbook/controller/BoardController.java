package com.project.insightbook.controller;

import com.project.insightbook.dto.BoardDto;
import com.project.insightbook.dto.BoardFileDto;
import com.project.insightbook.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;

@Controller
@Slf4j
public class BoardController {

    @Autowired
    private BoardService boardService;

    //게시글 목록
    @RequestMapping("/board/openBoardList.do")
    public ModelAndView openBoardList() throws Exception{
//        log.debug("BoardController -- openBoardList");
    /*ExceptionHandler 처리 여부 확인을 위해 띄운 에러 ServiceImpl 에 에러띄워도 잘 먹힘*/
//        int i= 10/0;
        ModelAndView mv = new ModelAndView("/board/boardList");
        List<BoardDto> list = boardService.selectBoardList();

        mv.addObject("list",list);

        return mv;
    }
    // 게시글 쓰기 페이지 이동
    @RequestMapping("/board/openBoardWrite.do")
    public String openBoardWrite() throws Exception{
        return "/board/boardWrite";
    }
    //게시글 쓰기
    @RequestMapping("/board/insertBoard.do")
    public String insertBoard(BoardDto boardDto, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
        boardService.insertBoard(boardDto, multipartHttpServletRequest);
        return "redirect:/board/openBoardList.do";
    }

    //게시글 상세보기
    @RequestMapping("/board/openBoardDetail.do")
    public ModelAndView openBoardDetail(@RequestParam int boardIdx) throws Exception{
        ModelAndView mv = new ModelAndView("/board/boardDetail");
        // transaction 을 serviceImpl 단에 선언하였기 때문에, 여기에 조회수 증가 메서드를 호출하게 되면
        // 페이지 오류가 나더라도 조회수가 증가하게 된다
        // 그래서 조회수증가는 게시글 상세보기를 선택하는 serviceImpl 의 메서드에서 호출하는게 맞다
        // boardService.updateHitCount(boardIdx);
        BoardDto board = boardService.selectBoardDetail(boardIdx);
        mv.addObject("board",board);
        return mv;
    }

    //게시글 업데이트
    @RequestMapping("/board/updateBoard.do")
    public String updateBoard(BoardDto boardDto) throws Exception{
        boardService.updateBoard(boardDto);
        return "redirect:/board/openBoardList.do";
    }
    //게시글 삭제

    @RequestMapping("/board/deleteBoard.do")
    public String deleteBoard(int boardIdx) throws Exception{
        boardService.deleteBoard(boardIdx);
        return "redirect:/board/openBoardList.do";
    }
    //파일다운로드
    @RequestMapping("/board/downloadBoardFile.do")
    public void downloadBoardFile(@RequestParam int idx, @RequestParam int boardIdx, HttpServletResponse response) throws Exception{
        BoardFileDto boardFile = boardService.selectBoardFileInformation(idx,boardIdx);
        if(ObjectUtils.isEmpty(boardFile) == false){
            String fileName = boardFile.getOriginalFileName();

            byte[] files = FileUtils.readFileToByteArray(new File(boardFile.getStoredFilePath()));

            response.setContentType("application/octet-stream");
            response.setContentLength(files.length);
            response.setHeader("Content-Disposition","attachment; fileName=\"" + URLEncoder.encode(fileName,"UTF-8") + "\";");
            response.setHeader("Content-Transfer-Encoding","binary");

            response.getOutputStream().write(files);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }

    }

}
