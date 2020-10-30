package com.golearn.controller;

import com.golearn.model.VideoComment;
import com.golearn.model.VideoCommentPayload;
import com.golearn.service.VideoCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api("VideoComment")
public class VideoCommentController {

    private VideoCommentService videoCommentService;

    VideoCommentController(VideoCommentService videoCommentService) {
        this.videoCommentService = videoCommentService;
    }

    @ApiOperation(value = "댓글 조회")
    @GetMapping("/comment/{vid_no}")
    public ResponseEntity<List<VideoCommentPayload>> getVideoComments(@PathVariable("vid_no") int vidNo, @RequestParam(value = "page_no", required = false, defaultValue = "0") int pageNo) {
        return new ResponseEntity(videoCommentService.getVideoComments(vidNo, pageNo), HttpStatus.OK);
    }

    @ApiOperation(value = "댓글 쓰기")
    @PostMapping("/comment")
    public ResponseEntity saveVideoComment(@RequestHeader("X-USERNO") int mbrNo, @RequestBody VideoComment videoComment) {
        videoComment.setMbrNo(mbrNo);
        videoCommentService.saveVideoComment(videoComment);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "대댓글, 댓글 수정")
    @PutMapping("/comment/{vid_cmt_no}")
    public ResponseEntity updateVideoComment(@RequestHeader("X-USERNO") int mbrNo, @PathVariable("vid_cmt_no") int vidCmtNo, @RequestBody VideoComment videoComment) {
        videoComment.setVidCmtNo(vidCmtNo);
        videoCommentService.updateVideoComment(videoComment, mbrNo);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "대댓글, 댓글 삭제")
    @DeleteMapping("/comment/{vid_cmt_no}")
    public ResponseEntity removeVideoComment(@RequestHeader("X-USERNO") int mbrNo, @PathVariable("vid_cmt_no") int vidCmtNo) {
        videoCommentService.removeVideoComment(vidCmtNo, mbrNo);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "대댓글 쓰기")
    @PostMapping("/comment/{vid_cmt_no}")
    public ResponseEntity saveVideoReply(@RequestHeader("X-USERNO") int mbrNo, @PathVariable("vid_cmt_no") int vidCmtNo, @RequestBody VideoComment videoComment) {
        videoComment.setMbrNo(mbrNo);
        videoComment.setVidCmtPno(vidCmtNo);
        videoCommentService.saveVideoComment(videoComment);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "대댓글 조회")
    @GetMapping("/comment/{vid_no}/{vid_cmt_no}")
    public ResponseEntity<List<VideoComment>> getVideoReplies(@PathVariable("vid_cmt_no") int vidCmtNo, @RequestParam(value = "page_no", required = false, defaultValue = "0") int pageNo) {
        return new ResponseEntity(videoCommentService.getVideoReplies(vidCmtNo, pageNo), HttpStatus.OK);
    }
}