package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity @Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers;

    @ManyToMany
    private Set<Account> members;

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Zone> tags;

    @ManyToMany
    private Set<Zone> zones;

    private LocalDateTime publishedDateTime; //공개한 시간
    private LocalDateTime closedDateTime; //모집종료시간
    private LocalDateTime recruitingUpdateDateTime; //제한시간(너무 자주 열고닫지 않게끔 인원모집을)

    private boolean recruiting; //현재 인원 모집중인지
    private boolean published; //공개했는지 안했는지
    private boolean closed;
    private boolean useBanner;


}
