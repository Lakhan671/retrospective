package com.sis.service;

import com.sis.entity.FeedbackItem;
import com.sis.entity.Retrospective;
import com.sis.repository.FeedbackItemRepository;
import com.sis.repository.RetrospectiveRepository;
import com.sis.utils.FeedbackType;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
@Service
@AllArgsConstructor
@Slf4j
public class RetrospectiveService {

    private final RetrospectiveRepository retrospectiveRepository;
    private final FeedbackItemRepository feedbackItemRepository;

    public Retrospective createRetrospective(Retrospective retrospective) {
        log.info("Received request to create a new retrospective: {}", retrospective);

        Optional.ofNullable(retrospective.getDate())
                .filter(date -> !retrospective.getParticipants().isEmpty())
                .orElseThrow(() -> {
                    log.error("Failed to create retrospective. Date and Participants are required");
                    return new IllegalArgumentException("Date and Participants are required");
                });

        Retrospective createdRetrospective = Optional.of(retrospective)
                .map(retrospectiveRepository::save)
                .orElseThrow(() -> {
                    log.error("Failed to create retrospective.");
                    return new RuntimeException("Failed to create retrospective.");
                });

        log.info("Retrospective created successfully. ID: {}", createdRetrospective.getId());
        return createdRetrospective;
    }

    public FeedbackItem addFeedbackItem(Long retrospectiveId, FeedbackItem feedbackItem) {
        log.info("Received request to add feedback item for retrospective with ID: {}", retrospectiveId);

        Retrospective retrospective = Optional.of(retrospectiveId)
                .map(this::getRetrospectiveById)
                .orElseThrow(() -> {
                    log.error("Failed to add feedback item. Retrospective not found with ID: {}", retrospectiveId);
                    return new EntityNotFoundException("Retrospective not found with ID: " + retrospectiveId);
                });

        feedbackItem.setRetrospective(retrospective);

        FeedbackItem addedFeedbackItem = Optional.of(feedbackItem)
                .map(feedbackItemRepository::save)
                .orElseThrow(() -> {
                    log.error("Failed to add feedback item.");
                    return new RuntimeException("Failed to add feedback item.");
                });

        log.info("Feedback item added successfully. ID: {}", addedFeedbackItem.getId());
        return addedFeedbackItem;
    }

    public FeedbackItem updateFeedbackItem(Long feedbackItemId, String body, FeedbackType feedbackType) {
        log.info("Received request to update feedback item with ID: {}", feedbackItemId);

        FeedbackItem updatedFeedbackItem = Optional.of(feedbackItemId)
                .map(this::getFeedbackItemById)
                .map(item -> {
                    item.setBody(body);
                    item.setFeedbackType(feedbackType);
                    return item;
                })
                .map(feedbackItemRepository::save)
                .orElseThrow(() -> {
                    log.error("Failed to update feedback item. Item not found with ID: {}", feedbackItemId);
                    return new EntityNotFoundException("Feedback item not found with ID: " + feedbackItemId);
                });

        log.info("Feedback item updated successfully. ID: {}", updatedFeedbackItem.getId());
        return updatedFeedbackItem;
    }

    public Page<Retrospective> getAllRetrospectives(int page, int pageSize) {
        return retrospectiveRepository.findAll(PageRequest.of(page, pageSize));
    }

    public Page<Retrospective> searchRetrospectivesByDate(LocalDate date, int page, int pageSize) {
        return retrospectiveRepository.findByDate(date, PageRequest.of(page, pageSize));
    }

    public Retrospective getRetrospectiveById(Long retrospectiveId) {
        return retrospectiveRepository.findById(retrospectiveId)
                .orElseThrow(() -> new EntityNotFoundException("Retrospective not found with id: " + retrospectiveId));
    }

    public FeedbackItem getFeedbackItemById(Long feedbackItemId) {
        return feedbackItemRepository.findById(feedbackItemId)
                .orElseThrow(() -> new EntityNotFoundException("FeedbackItem not found with id: " + feedbackItemId));
    }
}
