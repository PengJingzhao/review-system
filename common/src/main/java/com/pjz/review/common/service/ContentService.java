package com.pjz.review.common.service;

import com.pjz.review.common.entity.Content;
import com.pjz.review.common.entity.vo.ContentDetailVO;
import com.pjz.review.common.entity.vo.ContentVO;
import com.pjz.review.common.entity.vo.PageVO;
import com.pjz.review.common.entity.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/content")
public interface ContentService {

    @PostMapping("/createContent")
    Content createContent(@RequestBody Content content, @RequestHeader("authorization") String token);

    @GetMapping("/getContentById/{id}")
    Optional<Content> getContentById(@PathVariable("id") Long id);

    @GetMapping("/user/{userId}")
    List<Content> getContentsByUserId(@PathVariable("userId") Long userId);

    @PutMapping("/updateContent/{id}")
    Content updateContent(@PathVariable("id") Long id, Content updatedContent);

    @DeleteMapping("/deleteContent/{id}")
    void deleteContent(@PathVariable("id") Long id);

    @GetMapping("/getSelfPosts")
    PageVO<ContentVO> getSelfPosts(@RequestHeader("authorization") String token,
                                   @RequestParam("current") Long current,
                                   @RequestParam("size") Long size);

    @GetMapping("/getUserPosts/{userId}")
    PageVO<ContentVO> getUserPosts(
            @PathVariable("userId") Long userId
    );

    @GetMapping("/getSelfFollowerFeed")
    PageVO<ContentVO> getSelfFollowerFeed(@RequestHeader("authorization") String token,
                                          @RequestParam("current") Long current,
                                          @RequestParam("size") Long size);


    @GetMapping("/getContentDetail/{contentId}")
    ContentDetailVO getContentDetail(@PathVariable("contentId") Long contentId);

}
