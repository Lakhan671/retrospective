package com.sis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sis.entity.FeedbackItem;
import com.sis.entity.Retrospective;
import com.sis.repository.FeedbackItemRepository;
import com.sis.service.RetrospectiveService;
import com.sis.utils.FeedbackType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import static org.mockito.Mockito.doNothing;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;

import static org.mockito.ArgumentMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class RetrospectiveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetrospectiveService retrospectiveService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateRetrospective() throws Exception {
        Retrospective retrospective = createRetrospective("Sprint1", "Summary1", LocalDate.now(), Arrays.asList("Alice", "Bob"));

        Mockito.when(retrospectiveService.createRetrospective(ArgumentMatchers.any(Retrospective.class)))
                .thenReturn(retrospective);
        mockMvc.perform(post("/api/retrospectives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retrospective)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Sprint1")))
                .andExpect(jsonPath("$.summary", is("Summary1")))
                .andExpect(jsonPath("$.date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.participants", hasSize(2)))
                .andExpect(jsonPath("$.participants", contains("Alice", "Bob")));
    }

    @Test
    void testAddFeedbackItem() throws Exception {
        FeedbackItem feedbackItem = createFeedbackItem("Alice", "Great collaboration", FeedbackType.POSITIVE);
        Mockito.when(retrospectiveService.addFeedbackItem(anyLong(), ArgumentMatchers.any(FeedbackItem.class)))
                .thenReturn(feedbackItem);
        mockMvc.perform(post("/api/retrospectives/1/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackItem)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateFeedbackItem() throws Exception {
        Long feedbackItemId = 1L;
        FeedbackItem feedbackItem = createFeedbackItem("Alice", "Great collaboration", FeedbackType.POSITIVE);
        feedbackItem.setId(feedbackItemId);
        Mockito.when(retrospectiveService.getFeedbackItemById(Mockito.anyLong())).thenReturn(feedbackItem);
        Mockito.when(retrospectiveService.updateFeedbackItem(anyLong(), anyString(), ArgumentMatchers.any(FeedbackType.class))).thenReturn(feedbackItem);
        mockMvc.perform(put("/api/retrospectives/feedback/{feedbackItemId}", feedbackItemId)
                        .param("body", "Updated body")
                        .param("feedbackType", "IDEA"))
                .andExpect(status().isOk());
    }

    private Retrospective createRetrospective(String name, String summary, LocalDate date, List<String> participants) {
        Retrospective retrospective = new Retrospective();
        retrospective.setName(name);
        retrospective.setSummary(summary);
        retrospective.setDate(date);
        retrospective.setParticipants(participants);
        return retrospective;
    }

    private FeedbackItem createFeedbackItem(String feedbackProvider, String body, FeedbackType feedbackType) {
        FeedbackItem feedbackItem = new FeedbackItem();
        feedbackItem.setFeedbackProvider(feedbackProvider);
        feedbackItem.setBody(body);
        feedbackItem.setFeedbackType(feedbackType);
        return feedbackItem;
    }
}
