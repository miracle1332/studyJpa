package com.studyolle.event.form;

import com.studyolle.event.EventType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
@Data
public class EventForm { //알림양식

    @NotBlank
    @Length(max = 50)
    private String title; //제목

    private String description; //소개글

    private EventType eventType = EventType.FCFS; //알림타입 -

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime; //모임 접수 종료

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime; //시작일시

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime; //종료일시

    @Min(2)
    private Integer limitOfEnrollments = 2; //등록제한

}
