package com.ssafy.ttocket.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.ttocket.domain.PerformanceLike;
import com.ssafy.ttocket.domain.QPerformanceLike;
import com.ssafy.ttocket.domain.QUser;
import com.ssafy.ttocket.repository.PerformanceLikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.List;

public class PerformanceLikeRepositoryImpl extends QuerydslRepositorySupport implements PerformanceLikeRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public PerformanceLikeRepositoryImpl(EntityManager em) {
        super(PerformanceLike.class);
        this.queryFactory = new JPAQueryFactory(em);
    }

    QPerformanceLike qPerformanceLike = QPerformanceLike.performanceLike;

    @Override
    public List<PerformanceLike> findFirstListByUserId(String userId) {
        return queryFactory.selectFrom(qPerformanceLike)
                .where(qPerformanceLike.user.id.eq(userId))
                .limit(10)
                .fetch();
    }

    @Override
    public Page<PerformanceLike> findByCustom_cursorPaging(Pageable pageable, int cursorId, String userId) {

        List<PerformanceLike> performanceLikeList = queryFactory
                .selectFrom(qPerformanceLike)
                .where(qPerformanceLike.isLike.eq(Boolean.TRUE).and(qPerformanceLike.user.id.eq(userId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qPerformanceLike.likeId.performanceId)
                .from(qPerformanceLike)
                .fetchCount();

        return new PageImpl<>(performanceLikeList, pageable, total);
    }


}
