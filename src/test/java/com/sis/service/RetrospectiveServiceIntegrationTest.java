package com.sis.service;

import com.sis.entity.FeedbackItem;
import com.sis.entity.Retrospective;
import com.sis.repository.FeedbackItemRepository;
import com.sis.repository.RetrospectiveRepository;
import com.sis.utils.FeedbackType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@SpringBootTest
public class RetrospectiveServiceIntegrationTest {
    @Autowired
    private RetrospectiveService retrospectiveService;

    @MockBean
    private RetrospectiveRepository retrospectiveRepository;

    @MockBean
    private FeedbackItemRepository feedbackItemRepository;

    @Test
    public void testCreateRetrospective() {
        Retrospective retrospective = new Retrospective();
        retrospective.setName("Sprint1");
        retrospective.setSummary("Successful completion of the first sprint");
        retrospective.setDate(LocalDate.now());
        retrospective.setParticipants(Arrays.asList("Alice", "Bob", "Charlie"));
        Mockito.when(retrospectiveRepository.save(Mockito.any(Retrospective.class))).thenReturn(retrospective);
        Retrospective createdRetrospective = retrospectiveService.createRetrospective(retrospective);
        Assertions.assertNotNull(createdRetrospective);
        Assertions.assertEquals("Sprint1", createdRetrospective.getName());
        Assertions.assertEquals("Successful completion of the first sprint", createdRetrospective.getSummary());
        Assertions.assertEquals(LocalDate.now(), createdRetrospective.getDate());
        Assertions.assertEquals(Arrays.asList("Alice", "Bob", "Charlie"), createdRetrospective.getParticipants());
    }

    @Test
    public void testAddFeedbackItem() {
        Retrospective retrospective = new Retrospective();
        retrospective.setId(1L);
        FeedbackItem feedbackItem = new FeedbackItem();
        feedbackItem.setFeedbackProvider("Alice");
        feedbackItem.setBody("Great collaboration within the team");
        feedbackItem.setFeedbackType(FeedbackType.POSITIVE);
        Mockito.when(retrospectiveRepository.findById(1L)).thenReturn(Optional.of(retrospective));
        Mockito.when(feedbackItemRepository.save(Mockito.any(FeedbackItem.class))).thenReturn(feedbackItem);
        FeedbackItem addedFeedbackItem = retrospectiveService.addFeedbackItem(1L, feedbackItem);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(addedFeedbackItem),
                () -> Assertions.assertEquals("Alice", addedFeedbackItem.getFeedbackProvider()),
                ()-> Assertions.assertEquals("Great collaboration within the team", addedFeedbackItem.getBody()),
                ()->Assertions.assertEquals(FeedbackType.POSITIVE, addedFeedbackItem.getFeedbackType())
        );
    }
    @Test
    public void testUpdateFeedbackItem() {
        FeedbackItem feedbackItem = new FeedbackItem();
        feedbackItem.setId(1L);
        feedbackItem.setFeedbackProvider("Alice");
        feedbackItem.setBody("Great collaboration within the team");
        feedbackItem.setFeedbackType(FeedbackType.POSITIVE);

        Mockito.when(feedbackItemRepository.findById(1L)).thenReturn(Optional.of(feedbackItem));
        Mockito.when(feedbackItemRepository.save(Mockito.any(FeedbackItem.class))).thenReturn(feedbackItem);

        retrospectiveService.updateFeedbackItem(1L, "Improved communication", FeedbackType.IDEA);
        Assertions.assertAll(
                () -> Assertions.assertEquals("Improved communication", feedbackItem.getBody()),
                () -> Assertions.assertEquals(FeedbackType.IDEA, feedbackItem.getFeedbackType())
        );
    }

    @Test
    public void testGetAllRetrospectives() {
        Page<Retrospective> retrospectivePage = new PageImpl<>(Collections.emptyList());
        Mockito.when(retrospectiveRepository.findAll(Mockito.any(Pageable.class))).thenReturn(retrospectivePage);
        Page<Retrospective> result = retrospectiveService.getAllRetrospectives(0, 10);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getContent().size());
    }

    @Test
    public void testSearchRetrospectivesByDate() {
        LocalDate date = LocalDate.now();
        Page<Retrospective> retrospectivePage = new PageImpl<>(Collections.emptyList());
        Mockito.when(retrospectiveRepository.findByDate(Mockito.any(LocalDate.class), Mockito.any(Pageable.class))).thenReturn(retrospectivePage);
        Page<Retrospective> result = retrospectiveService.searchRetrospectivesByDate(date, 0, 10);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getContent().size());
    }

    @Test
    public void testGetRetrospectiveById() {
        Retrospective retrospective = new Retrospective();
        retrospective.setId(1L);
        Mockito.when(retrospectiveRepository.findById(1L)).thenReturn(Optional.of(retrospective));
        Retrospective result = retrospectiveService.getRetrospectiveById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId().longValue());
    }
    @Test
    public void testGetRetrospectiveByIdNotFound() {
        Mockito.when(retrospectiveRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(feedbackItemRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            retrospectiveService.getRetrospectiveById(1L);
        });
    }

    @Test
    public void testGetFeedbackItemById() {
        FeedbackItem feedbackItem = new FeedbackItem();
        feedbackItem.setId(1L);
        Mockito.when(feedbackItemRepository.findById(1L)).thenReturn(Optional.of(feedbackItem));
        FeedbackItem result = retrospectiveService.getFeedbackItemById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId().longValue());
    }

    @Test
    public void testGetFeedbackItemByIdNotFound() {
        Mockito.when(feedbackItemRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            retrospectiveService.getFeedbackItemById(1L);
        });
    }

}
