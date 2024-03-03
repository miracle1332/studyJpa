package com.studyolle.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study; //어느 스터디에 속한 이벤트인지

    @ManyToOne
    private Account account; //모임을 만든 사람을 알 수 있게 누가 만들었느냐

    @Lob
    private String description;

    private LocalDateTime createDateTime;



}

