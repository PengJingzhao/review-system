package com.pjz.review.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pjz.commons.constants.CountConstants;
import com.pjz.commons.constants.UserContentRelationConstants;
import com.pjz.review.common.entity.dto.QuestionCreateRequest;
import com.pjz.review.common.entity.dto.QuestionPageRequest;
import com.pjz.review.common.entity.po.*;
import com.pjz.review.common.service.*;
import com.pjz.review.question.mapper.CommentMapper;
import com.pjz.review.question.mapper.QuestionMapper;
import com.pjz.review.question.mapper.QuestionTagMapper;
import com.pjz.review.question.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@DubboService
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final QuestionTagMapper questionTagMapper;

    private final CommentMapper commentMapper;

    private final TagMapper tagMapper;

    @DubboReference
    private UserService userService;

    @DubboReference
    private BizCountService countService;

    @DubboReference
    private UserContentRelationService userContentRelationService;

    @Override
    @Transactional
    public Question createQuestion(QuestionCreateRequest createRequest) {
// 构建并保存题目信息，MyBatis-Plus自动回填ID
        Question question = Question.builder()
                .title(createRequest.getTitle())
                .answer(createRequest.getAnswer())
                .source(createRequest.getSource())
                .appearRate(createRequest.getAppearRate())
                .difficulty(createRequest.getDifficulty())
                .favoriteCount(0)
                .likeCount(0)
                .commentCount(0)
                .viewCount(0)
                .build();
        this.baseMapper.insert(question);

        // 处理标签关系，支持批量插入，避免循环插入数据库
        List<Long> tagIds = createRequest.getTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<QuestionTag> questionTags = tagIds.stream()
                    .map(tagId -> {
                        QuestionTag questionTag = new QuestionTag();
                        questionTag.setQuestionId(question.getId());
                        questionTag.setTagId(tagId);
                        return questionTag;
                    })
                    .collect(Collectors.toList());
            // 这里假设 questionTagMapper 支持批量插入方法，如 insertBatch
            questionTagMapper.insertBatch(questionTags);
        }

        return question;
    }

    @Override
    public IPage<Question> pageQuestionList(QuestionPageRequest params) {
        // 规范分页参数，默认值：page=1，size=10
        int current = (params.getPage() != null && params.getPage() > 0) ? params.getPage() : 1;
        int size = (params.getSize() != null && params.getSize() > 0) ? params.getSize() : 10;
        Page<Question> page = new Page<>(current, size);

        QueryWrapper<Question> query = new QueryWrapper<>();

        // 按难度过滤
        if (params.getDifficulty() != null) {
            query.eq("difficulty", params.getDifficulty());
        }

        // 按关键词模糊搜索title和answer
        String keyword = params.getKeyword();
        if (StringUtils.hasText(keyword)) {
            query.and(wrapper -> wrapper.like("title", keyword).or().like("answer", keyword));
        }

        // 按标签过滤（题目必须至少含有tags中的一个）
        List<Long> filterTagIds = params.getTags();
        if (!CollectionUtils.isEmpty(filterTagIds)) {
            // 查询匹配标签的 question_id 列表
            QueryWrapper<QuestionTag> tagQuery = new QueryWrapper<>();
            tagQuery.in("tag_id", filterTagIds);
            List<Long> questionIds = questionTagMapper.selectList(tagQuery).stream()
                    .map(QuestionTag::getQuestionId)
                    .distinct()
                    .collect(Collectors.toList());

            if (questionIds.isEmpty()) {
                // 没有符合标签条件的，直接返回空页
                page.setRecords(Collections.emptyList());
                page.setTotal(0);
                return page;
            }
            query.in("id", questionIds);
        }

        // 分页查询符合条件的题目列表
        // todo 采用缓存优化慢sql
        IPage<Question> resultPage = this.page(page, query);
        List<Question> questions = resultPage.getRecords();
//        Page<Question> resultPage = new Page<>();
//        List<Question> questions = this.baseMapper.selectQuestionsByTagsPage(page, (current - 1) * size, size, params.getDifficulty(), params.getKeyword(), params.getTags());
//        resultPage.setRecords(questions)
//                .setTotal(this.count(query))
//                .setSize(size)
//                .setCurrent(current);


        if (!questions.isEmpty()) {
            List<Long> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());

            // 批量查询所有这些题目的标签映射
            QueryWrapper<QuestionTag> tagMapQuery = new QueryWrapper<>();
            tagMapQuery.in("question_id", questionIds);
            List<QuestionTag> allQuestionTags = questionTagMapper.selectList(tagMapQuery);

            // 去重标签ID批量查询对应标签内容，避免每题标签再单独查库（N+1问题）
            Set<Long> allTagIds = allQuestionTags.stream()
                    .map(QuestionTag::getTagId)
                    .collect(Collectors.toSet());

            Map<Long, String> tagIdToName = new HashMap<>();
            if (!allTagIds.isEmpty()) {
                tagIdToName = tagMapper.selectBatchIds(new ArrayList<>(allTagIds)).stream()
                        .collect(Collectors.toMap(Tag::getId, Tag::getTag));
            }

            // 按题目ID聚合标签列表并设置到题目对象上
            Map<Long, List<String>> questionIdToTags = new HashMap<>();
            for (QuestionTag qt : allQuestionTags) {
                questionIdToTags.computeIfAbsent(qt.getQuestionId(), k -> new ArrayList<>())
                        .add(tagIdToName.getOrDefault(qt.getTagId(), ""));
            }

            questions.forEach(q -> q.setTags(questionIdToTags.getOrDefault(q.getId(), Collections.emptyList())));
        }

        return resultPage;
    }

    @Override
    public Question getQuestionDetail(Integer questionId) {
        Question question = this.baseMapper.selectById(questionId);
        if (question == null) {
            return null;
        }

        // 查询标签
        QueryWrapper<QuestionTag> tagWrapper = new QueryWrapper<>();
        tagWrapper.eq("question_id", questionId);
        List<QuestionTag> tags = questionTagMapper.selectList(tagWrapper);
        List<String> tagList = tags.stream()
                .map(questionTag -> tagMapper.selectById(questionTag.getTagId()).getTag())
                .collect(Collectors.toList());
        question.setTags(tagList);

        // 查询评论
        QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("question_id", questionId).orderByDesc("create_time");
        List<Comment> commentList = commentMapper.selectList(commentWrapper);

        question.setComments(commentList);

        return question;
    }

    @Override
    public List<Tag> getTags() {

        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Tag::getId, Tag::getTag);

        return tagMapper.selectList(wrapper);
    }

    @Override
    public IPage<Question> getQuestionByTag(Long tagId, Integer page, Integer size) {
        QuestionPageRequest request = new QuestionPageRequest();
        request.setPage(page);
        request.setSize(size);

        List<Long> tags = new ArrayList<>();
        tags.add(tagId);
        request.setTags(tags);
        return pageQuestionList(request);
    }

    @Override
    public Long getNextQuestionId(Long currentId, Long tagId) {

        // 首先查出当前标签下的所有题目
        List<Long> questionIds = questionTagMapper.getQuestionsByTagId(tagId);

        // 根据当前题目来找出下一个题目
        for (int i = 0; i < questionIds.size() - 1; i++) {
            if (questionIds.get(i).equals(currentId)) {
                return questionIds.get(i + 1);
            }
        }

        return 0L;
    }

    @Override
    public Long getPrevQuestionId(Long currentId, Long tagId) {
        // 首先查出当前标签下的所有题目
        List<Long> questionIds = questionTagMapper.getQuestionsByTagId(tagId);

        // 根据当前题目来找出下一个题目
        for (int i = 1; i < questionIds.size(); i++) {
            if (questionIds.get(i).equals(currentId)) {
                return questionIds.get(i - 1);
            }
        }

        return 0L;
    }

    @Override
    public Long like(Long questionId, String token) {
        Long userId = userService.getUser(token).getId();
        // 判断是否点过赞了
        if (userContentRelationService.relationExists(userId, questionId, UserContentRelationConstants.LIKE)) {
            // 取消点赞
            userContentRelationService.removeRelation(userId, questionId, UserContentRelationConstants.LIKE);

            // 计数服务增加点赞数
            countService.incrementCount(CountConstants.QUESTION, String.valueOf(questionId), CountConstants.LIKE, -1);
            // 查询点赞数
            BizCount count = countService.getCount(CountConstants.QUESTION, String.valueOf(questionId), CountConstants.LIKE);

            return count.getCountValue();
        }

        UserContentRelation relation = UserContentRelation.builder()
                .contentId(questionId)
                .userId(userId)
                .relationType(UserContentRelationConstants.LIKE)
                .build();

        userContentRelationService.addRelation(relation);

        // 计数服务增加点赞数
        countService.incrementCount(CountConstants.QUESTION, String.valueOf(questionId), CountConstants.LIKE, 1);

        BizCount newCount = countService.getCount(CountConstants.QUESTION, String.valueOf(questionId), CountConstants.LIKE);
        return newCount.getCountValue();
    }
}
