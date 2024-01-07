package com.sis.controller;

import com.sis.entity.FeedbackItem;
import com.sis.entity.Retrospective;
import com.sis.service.RetrospectiveService;
import com.sis.utils.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/retrospectives")
@Slf4j
@AllArgsConstructor
public class RetrospectiveController {

    private RetrospectiveService retrospectiveService;
    @PostMapping
    public ResponseEntity<Retrospective> createRetrospective(@RequestBody Retrospective retrospective) {
        log.info("createRetrospective method call, request body = {}", retrospective);
       return Optional.ofNullable(retrospective)
                .map(retrospectiveService::createRetrospective)
                .map(createdRetrospective -> {
                    log.info("created Retrospective body = {}", createdRetrospective);
                    return new ResponseEntity<>(createdRetrospective, HttpStatus.CREATED);
                })
                .orElseThrow(() -> {
                    log.error("Failed to create retrospective. Request body is null.");
                    return new IllegalArgumentException("Request body is null.");
                });

    }


    @PostMapping("/{retrospectiveId}/feedback")
    public ResponseEntity<FeedbackItem> addFeedbackItem(@PathVariable Long retrospectiveId, @RequestBody FeedbackItem feedbackItem) {
        log.info("Received request to add feedback item for retrospective with ID: {}", retrospectiveId);

       return Optional.of(retrospectiveId)
                .map(id -> retrospectiveService.addFeedbackItem(id, feedbackItem))
                .map(addedFeedbackItem -> {
                    log.info("Feedback item added successfully. ID: {}", addedFeedbackItem.getId());
                    return new ResponseEntity<>(addedFeedbackItem, HttpStatus.CREATED);
                })
                .orElseThrow(() -> {
                    log.error("Failed to add feedback item. Retrospective ID is null.");
                    return new IllegalArgumentException("Retrospective ID is null.");
                });
    }


    @PutMapping("/feedback/{feedbackItemId}")
    public ResponseEntity<FeedbackItem> updateFeedbackItem(@PathVariable Long feedbackItemId,
                                                           @RequestParam String body,
                                                           @RequestParam FeedbackType feedbackType) {
        log.info("Received request to update feedback item with ID: {}", feedbackItemId);

        return Optional.of(feedbackItemId)
                .map(id -> retrospectiveService.updateFeedbackItem(id, body, feedbackType))
                .map(updatedFeedbackItem -> {
                    log.info("Feedback item updated successfully. ID: {}", updatedFeedbackItem.getId());
                    return new ResponseEntity<>(updatedFeedbackItem, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Failed to update feedback item with ID: {}", feedbackItemId);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @GetMapping
    public ResponseEntity<Page<Retrospective>> getAllRetrospectives(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Received request to retrieve all retrospectives (page: {}, pageSize: {})", page, pageSize);
       return   Optional.of(PageRequest.of(page, pageSize))
                .map(request -> retrospectiveService.getAllRetrospectives(page,pageSize))
                .map(retrospectives -> {
                    log.info("Retrieved {} retrospectives successfully", retrospectives.getTotalElements());
                    return new ResponseEntity<>(retrospectives, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Failed to retrieve retrospectives");
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Retrospective>> searchRetrospectivesByDate(
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        log.info("Received request to search retrospectives by date (date: {}, page: {}, pageSize: {})", date, page, pageSize);

       return Optional.of(date)
                .map(d -> retrospectiveService.searchRetrospectivesByDate(d, page, pageSize))
                .map(retrospectives -> {
                    log.info("Found {} retrospectives for date: {}", retrospectives.getTotalElements(), date);
                    return new ResponseEntity<>(retrospectives, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Failed to search retrospectives by date");
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

}
