package com.sis.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sis.utils.FeedbackType;
import jakarta.persistence.*;
import lombok.Data;

// FeedbackItem.java
// FeedbackItem.java
@Entity
@Data
public class FeedbackItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feedbackProvider;

    private String body;

    @Enumerated(EnumType.STRING)
    private FeedbackType feedbackType;

    @ManyToOne
    @JoinColumn(name = "retrospective_id")
    @JsonBackReference
    private Retrospective retrospective;

}
