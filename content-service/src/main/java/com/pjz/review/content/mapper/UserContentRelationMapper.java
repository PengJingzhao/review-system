package com.pjz.review.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.common.entity.po.UserContentRelation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserContentRelationMapper extends BaseMapper<UserContentRelation> {

    // 查询某用户某内容某类型关系
    default UserContentRelation selectRelation(Long userId, Long contentId, String relationType) {
        LambdaQueryWrapper<UserContentRelation> query = new LambdaQueryWrapper<>();
        query.eq(UserContentRelation::getUserId, userId)
                .eq(UserContentRelation::getContentId, contentId)
                .eq(UserContentRelation::getRelationType, relationType);
        return selectOne(query);
    }

    // 查询某用户所有指定类型关系
    default List<UserContentRelation> selectByUserAndType(Long userId, String relationType) {
        LambdaQueryWrapper<UserContentRelation> query = new LambdaQueryWrapper<>();
        query.eq(UserContentRelation::getUserId, userId)
                .eq(UserContentRelation::getRelationType, relationType);
        return selectList(query);
    }

    // 删除指定关系
    default int deleteRelation(Long userId, Long contentId, String relationType) {
        LambdaUpdateWrapper<UserContentRelation> update = new LambdaUpdateWrapper<>();
        update.eq(UserContentRelation::getUserId, userId)
                .eq(UserContentRelation::getContentId, contentId)
                .eq(UserContentRelation::getRelationType, relationType);
        return delete(update);
    }
}
