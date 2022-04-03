package com.project.insightbook.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoardFileDto {
    private int idx;
    private int boardIdx;
    private String originalFileName;
    private String storedFilePath;
    private long fileSize;


}
