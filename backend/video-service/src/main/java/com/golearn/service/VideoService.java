package com.golearn.service;

import com.golearn.exception.UnAuthorizationException;
import com.golearn.model.Video;
import com.golearn.model.VideoCompositeKey;
import com.golearn.model.VideoLike;
import com.golearn.repository.TagRepository;
import com.golearn.repository.VideoLikeRepository;
import com.golearn.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VideoService {
    private final VideoRepository videoRepository;

    private final VideoLikeRepository videoLikeRepository;

    private final TagRepository tagRepository;
    VideoService(VideoRepository videoRepository, VideoLikeRepository videoLikeRepository, TagRepository tagRepository) {
        this.videoRepository = videoRepository;
        this.videoLikeRepository = videoLikeRepository;
        this.tagRepository = tagRepository;
    }

    public Video getVideo(int vidNo) {
        if (videoRepository.existsById(vidNo)) {
            videoRepository.addViewCount(vidNo);
            Video video = videoRepository.findById(vidNo).get();
            video.setTags(tagRepository.findAllAndTagByVidNo(video.getVidNo()));
            return video;
        }
        return null;
    }

    public void hideVideo(List<Integer> vidNos, int mbrNo) {
        if (videoRepository.hideVideo(vidNos, mbrNo) == 0) {
            throw new UnAuthorizationException();
        }
    }

    public void saveVideo(int mbrNo) {

    }

    public Map isLikeVideo(int vidNo, int mbrNo) {
        Map<String, Boolean> result = new HashMap();
        result.put("isLike", videoLikeRepository.existsById(new VideoCompositeKey(vidNo, mbrNo)));
        return result;
    }

    public void likeVideo(int vidNo, int mbrNo) {
        VideoCompositeKey videoCompositekey = new VideoCompositeKey(vidNo, mbrNo);
        if (!videoLikeRepository.existsById(videoCompositekey)) {
            VideoLike videoLike = new VideoLike(videoCompositekey);
            videoLikeRepository.save(videoLike);
        }
    }

    public void unlikeVideo(int vidNo, int mbrNo) {
        VideoCompositeKey videoCompositekey = new VideoCompositeKey(vidNo, mbrNo);
        if (videoLikeRepository.existsById(videoCompositekey)) {
            videoLikeRepository.deleteById(videoCompositekey);
        }
    }

    public List<Video> getLikeVideo(int mbrNo) {
        return videoRepository.findAllByMbrNoAndLikeVideoAndVidHideFalse(mbrNo);
    }

    public List<Video> getVideos(int mbrNo) {
        List<Video> videos = videoRepository.findAllByMbrNoAndVidHideFalse(mbrNo);
        for(Video video: videos){
            video.setTags(tagRepository.findAllAndTagByVidNo(video.getVidNo()));
        }
        return videos;
    }
}
//
