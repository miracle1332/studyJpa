package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
        name = "Event.withEnrollments",
        attributeNodes = @NamedAttributeNode("enrollments") // Event 엔티티를 조회할 때 관련된 enrollments도 함께 즉시 로딩
)
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne //다대일 단방향// 이벤트는 여러개일수 있고 스터디모임은 하나임.
    private Study study;

    @ManyToOne  //다대일 단방향
    private Account creatBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false) //모임생성일시
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime; //모임등록일시

    @Column(nullable = false)
    private LocalDateTime startDateTime; //시작일시

    @Column(nullable = false)
    private LocalDateTime endDateTime; //종료일시

    @Column
    private Integer limitOfEnrollments; //등록제한

    @OneToMany(mappedBy = "event") //일대다 양방향//Event엔티티는 주인이아니고 Enrollments엔티티가 주인.
    @OrderBy("enrolledAt")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING) //열거형(Enum) 필드를 매핑할 때 사용하는 어노테이션입니다. 이 어노테이션은 열거형 상수를 데이터베이스에 문자열 형태로 저장하고자 할 때 사용됩니다.
    private EventType eventType;


}
