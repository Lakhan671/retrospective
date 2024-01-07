package com.sis.repository;

import com.sis.entity.FeedbackItem;
import com.sis.entity.Retrospective;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {
    Page<Retrospective> findAll(Pageable pageable);
    Page<Retrospective> findByDate(LocalDate date, Pageable pageable);
}


