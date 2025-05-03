package com.pjz.review.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pjz.review.common.entity.dto.QuestionCreateRequest;
import com.pjz.review.common.entity.dto.QuestionPageRequest;
import com.pjz.review.common.entity.po.Comment;
import com.pjz.review.common.entity.po.Question;
import com.pjz.review.common.entity.po.QuestionTag;
import com.pjz.review.common.service.QuestionService;
import com.pjz.review.question.mapper.CommentMapper;
import com.pjz.review.question.mapper.QuestionMapper;
import com.pjz.review.question.mapper.QuestionTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@DubboService
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final QuestionTagMapper questionTagMapper;

    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public Question createQuestion(QuestionCreateRequest createRequest) {
        // 先保存题目基本信息
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

        log.info("插入数据库Question:{}", question);

        // 插入数据库，mybatisplus会自动设置ID返回
        this.baseMapper.insert(question);

        Long questionId = question.getId();
        log.info("获得questionId：{}", questionId);

        if (createRequest.getTags() != null && !createRequest.getTags().isEmpty()) {
            // 批量保存标签
            List<QuestionTag> tagList = createRequest.getTags().stream()
                    .filter(tag -> tag != null && !tag.trim().isEmpty())
                    .map(tag -> QuestionTag.builder()
                            .questionId(questionId)
                            .tag(tag.trim())
                            .build())
                    .toList();

            // 批量插入标签
            log.info("插入标签:{}", tagList);
            tagList.forEach(questionTagMapper::insert);
        }


        return question;
    }

    @Override
    public IPage<Question> pageQuestionList(QuestionPageRequest params) {
        int current = params.getPage() != null && params.getPage() > 0 ? params.getPage() : 1;
        int size = params.getSize() != null && params.getSize() > 0 ? params.getSize() : 10;

        Page<Question> page = new Page<>(current, size);

        QueryWrapper<Question> query = new QueryWrapper<>();

        // 按难度过滤
        if (params.getDifficulty() != null) {
            query.eq("difficulty", params.getDifficulty());
        }

        // 按关键词模糊搜索title和answer
        if (StringUtils.hasText(params.getKeyword())) {
            query.and(wrapper -> wrapper.like("title", params.getKeyword()).or().like("answer", params.getKeyword()));
        }

        // 按标签过滤(题目必须至少含有tags中的一个，示例用IN语句)
        if (!CollectionUtils.isEmpty(params.getTags())) {
            // 先查question_ids 满足标签条件 ——标签表中任一匹配的question_id列表
            QueryWrapper<QuestionTag> tagQuery = new QueryWrapper<>();
            tagQuery.in("tag", params.getTags());
            List<QuestionTag> questionTags = questionTagMapper.selectList(tagQuery);

            List<Long> questionIds = questionTags.stream()
                    .map(QuestionTag::getQuestionId)
                    .distinct()
                    .collect(Collectors.toList());

            if (questionIds.isEmpty()) {
                // 直接返回空页
                page.setRecords(List.of());
                page.setTotal(0);
                return page;
            }
            query.in("id", questionIds);
        }

        log.info("查询参数:{},{}", page, query);

        IPage<Question> resultPage = this.page(page, query);

        // 查询标签列表并塞入每个题目（可选，根据实际需求）
        List<Question> questions = resultPage.getRecords();
        if (!questions.isEmpty()) {
            List<Long> qids = questions.stream().map(Question::getId).collect(Collectors.toList());

            QueryWrapper<QuestionTag> tagQuery = new QueryWrapper<>();
            tagQuery.in("question_id", qids);
            List<QuestionTag> allTags = questionTagMapper.selectList(tagQuery);

            // 组装标签
            questions.forEach(q -> {
                List<String> relatedTags = allTags.stream()
                        .filter(t -> t.getQuestionId().equals(q.getId()))
                        .map(QuestionTag::getTag)
                        .collect(Collectors.toList());
                q.setTags(relatedTags);
            });
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
        List<String> tagList = tags.stream().map(QuestionTag::getTag).collect(Collectors.toList());
        question.setTags(tagList);

        // 查询评论
        QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("question_id", questionId).orderByDesc("create_time");
        List<Comment> commentList = commentMapper.selectList(commentWrapper);

        question.setComments(commentList);

        return question;
    }
}
