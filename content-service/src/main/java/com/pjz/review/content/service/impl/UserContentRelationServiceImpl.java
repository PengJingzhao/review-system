package com.pjz.review.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pjz.review.common.entity.po.UserContentRelation;
import com.pjz.review.common.service.UserContentRelationService;
import com.pjz.review.content.mapper.UserContentRelationMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@DubboService
public class UserContentRelationServiceImpl
        extends ServiceImpl<UserContentRelationMapper, UserContentRelation>
        implements UserContentRelationService {

    @Resource
    private UserContentRelationMapper userContentRelationMapper;

    @Override
    @Transactional
    public boolean addRelation(UserContentRelation relation) {
        // 先判断是否存在，避免重复插入
        UserContentRelation exist = baseMapper.selectRelation(
                relation.getUserId(),
                relation.getContentId(),
                relation.getRelationType());
        if (exist != null) {
            return false;
        }
        relation.setCreatedAt(LocalDateTime.now());
        relation.setUpdatedAt(LocalDateTime.now());
        return save(relation);
    }

    @Override
    @Transactional
    public boolean removeRelation(Long userId, Long contentId, String relationType) {
        int deleted = baseMapper.deleteRelation(userId, contentId, relationType);
        return deleted > 0;
    }

    @Override
    public UserContentRelation getRelation(Long userId, Long contentId, String relationType) {
        return baseMapper.selectRelation(userId, contentId, relationType);
    }

    @Override
    public List<UserContentRelation> getRelationsByUser(Long userId, String relationType) {
        return baseMapper.selectByUserAndType(userId, relationType);
    }

    @Override
    public List<UserContentRelation> getRelationsByContent(Long contentId, String relationType) {
        // 方法需要自定义实现，示例如下：
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserContentRelation>()
                .eq(UserContentRelation::getContentId, contentId)
                .eq(UserContentRelation::getRelationType, relationType));
    }

    @Override
    public boolean relationExists(Long userId, Long contentId, String relationType) {
        return userContentRelationMapper.relationExists(userId,contentId,relationType);
    }
}
