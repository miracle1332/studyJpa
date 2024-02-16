package com.studyolle.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Tag { //초기버전으로 심플하게 구현 //어떤 유저가 어떤 태그(관심주제)를 가지고있는지 기능 -> 어카운트와 태그와 다대다관계

     @Id @GeneratedValue
    private Long id;

     @Column(unique = true, nullable = false)
    private String title;

}
