package org.example.expert.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.response.ImageResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.example.expert.domain.user.entity.QUserImage;
import org.springframework.data.domain.*;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPQLQueryFactory jpqlQueryFactory;

    @Override
    public Slice<UserResponse> findUsersByNickName(String nickName, Pageable pageable) {
        QUser user = QUser.user;
        QUserImage userImage = QUserImage.userImage;

        List<UserResponse> users = jpqlQueryFactory
                .select(Projections.constructor(UserResponse.class
                        ,user.id
                        , user.email
                        , Projections.constructor(ImageResponse.class, userImage.url, userImage.fileName )))
                .from(user)
                .leftJoin(userImage).on(user.eq(userImage.user))//1:1 관계
                .where(hasNickName(nickName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();
        //pageSize보다 크다면 다음페이지도 존재
        boolean hasNext = pageable.getPageSize() < users.size();
        if(hasNext) {
            users.remove(users.size()-1);
        }

        return new SliceImpl<>(users, pageable, hasNext);
    }

    private BooleanExpression hasNickName(String nickName) {
        QUser user = QUser.user;
        if(nickName == null || nickName.isEmpty()) {
            return Expressions.TRUE;
        }

        return user.nickName.eq(nickName);
    }
}
