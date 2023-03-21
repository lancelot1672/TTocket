package com.ssafy.ttocket.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.ttocket.domain.Performance;
import com.ssafy.ttocket.domain.PerformanceLike;
import com.ssafy.ttocket.domain.QPerformance;
import com.ssafy.ttocket.repository.PerformanceLikeRepository;
import com.ssafy.ttocket.repository.PerformanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.List;

import static com.ssafy.ttocket.domain.QPerformance.performance;

public class PerformanceRepositoryImpl extends QuerydslRepositorySupport implements PerformanceRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PerformanceRepositoryImpl(EntityManager em) {
        super(Performance.class);
        this.queryFactory = new JPAQueryFactory(em);
    }
    QPerformance qPerformance = performance;

    @Override
    public List<Performance> findOpenSoon() {
        return queryFactory
                .selectFrom(performance)
                .offset(10)
                .fetch();
    }
    @Override
    public List<Performance> findPerformSoon() {
        return queryFactory
                .selectFrom(qPerformance)
                .orderBy(qPerformance.endTime.desc())
                .offset(10)
                .fetch();
    }

    @Override
    public Page<Performance> findByCustom_cursorPaging(Pageable pageable, int cursorId) {

        List<Performance> performanceList = queryFactory
                .selectFrom(performance)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(performance.id)
                .from(performance)
                .fetchCount();

        return new PageImpl<>(performanceList, pageable, total);
    }
}
