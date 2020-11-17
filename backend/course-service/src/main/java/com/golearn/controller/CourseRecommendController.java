package com.golearn.controller;

import com.golearn.domain.Course;
import com.golearn.service.CourseRecommendService;
import io.swagger.annotations.Api;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Api("CourseRecommend")
public class CourseRecommendController {

    private final CourseRecommendService courseRecommendService;
    CourseRecommendController(CourseRecommendService courseRecommendService){
        this.courseRecommendService = courseRecommendService;
    }

    @GetMapping("/recommendation")
    public ResponseEntity<List<Course>> getRecommendCourse(@RequestHeader("X-USERNO") int mbrNo) throws TasteException, IOException {
        return new ResponseEntity(courseRecommendService.getRecommendCourse(mbrNo), HttpStatus.OK);
    }
}
