package com.health.model;

import jakarta.persistence.*;

@Entity
@Table(name = "setup_questions")
public class SetupQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @Column(length = 1000)
    private String options; // comma-separated options e.g. "Lose Weight,Build Muscle,Stay Fit"

    private Integer questionOrder; // 1,2,3,4,5,6
    private String status = "Active";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
