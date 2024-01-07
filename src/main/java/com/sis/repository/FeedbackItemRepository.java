package com.sis.repository;

import com.sis.entity.FeedbackItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackItemRepository extends JpaRepository<FeedbackItem, Long> {
}