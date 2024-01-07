package com.sis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
public class Retrospective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String summary;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "date")
    private LocalDate date;

    @ElementCollection
    private List<String> participants;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "retrospective_id")
    @JsonManagedReference
    private List<FeedbackItem> feedbackItems = new ArrayList<>();

}

