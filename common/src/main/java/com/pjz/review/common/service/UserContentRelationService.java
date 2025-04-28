package com.pjz.review.common.service;

import com.pjz.review.common.entity.po.UserContentRelation;

import java.util.List;

public interface UserContentRelationService {

    boolean addRelation(UserContentRelation relation);

    boolean removeRelation(Long userId, Long contentId, String relationType);

    UserContentRelation getRelation(Long userId, Long contentId, String relationType);

    List<UserContentRelation> getRelationsByUser(Long userId, String relationType);

    List<UserContentRelation> getRelationsByContent(Long contentId, String relationType);

    boolean relationExists(Long userId, Long contentId, String relationType);
}
