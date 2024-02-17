package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue //엔티티
    private Long id;

    ///value - 밸류는 라이프사이클이 엔티티에 종속되어 있다. 독자적인 라이프사이클이 없다.
    @Column(unique = true) //유일키
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified; //이메일이 인증된 계정인지 아닌지 확인할 수 있는 필드

    private String emailCheckToken;//이메일 검증할때 사용할 토큰 값

    private LocalDateTime joinedAt; //가입날짜

    private LocalDateTime emailCheckTokenGeneratedAt;

    private String bio; //자기소개

    private  String url; //웹사이트 url

    private String occupation;//직업

    private String location; //어디근처 살고 있는지

    @Lob @Basic(fetch = FetchType.EAGER) //즉시로딩으로
    private String profileImage;

    private boolean studyCreatedByEmail; //스터디 오픈소식을 이메일로 받을것인가

    private boolean studyCreatedByWeb = true; //웹으로 받을 것인가

    private boolean studyEnrollmentResultByEmail; //스터디 가입신청 결과를 이메일로 받을것인가

    private boolean studyEnrollmentResultByWeb = true; //웹으로 받을것인가

    private boolean studyUpdateByEmail; //스터디 갱신 정보

    private boolean studyUpdateByWeb = true;

    @ManyToMany
    private Set<Tag> tags; //set, list차이

    public void generateEmailCheckToken()
    {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

}
