package com.ssafy.ttocket.repository;

import com.ssafy.ttocket.domain.Performance;
import com.ssafy.ttocket.domain.PerformanceLike;
import com.ssafy.ttocket.repository.querydsl.PerformanceRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PerformanceRepository extends JpaRepository<Performance, Integer>, PerformanceRepositoryCustom {
    Performance findById(int id);
}
