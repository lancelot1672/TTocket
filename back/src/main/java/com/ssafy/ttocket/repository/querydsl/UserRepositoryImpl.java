package com.ssafy.ttocket.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.ttocket.domain.Performance;
import com.ssafy.ttocket.domain.QPerformance;
import com.ssafy.ttocket.domain.Seat;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;

import java.util.List;

import static com.ssafy.ttocket.domain.QPerformance.performance;

public class UserRepositoryImpl extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        super(Performance.class);
        this.queryFactory = new JPAQueryFactory(em);
    }
}
