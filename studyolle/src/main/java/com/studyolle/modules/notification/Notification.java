package com.studyolle.modules.notification;

import com.studyolle.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    private String title; //알림 제목

    private String link; //알림 링크

    private String message; //알림 메세지

    private boolean checked; //알림 확인여부

    @ManyToOne
    private Account account; //계정 다대일

    private LocalDateTime createdDateTime; //알림생성일시

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; //알림타입

}