package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity @Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Zone> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime; //공개한 시간
    private LocalDateTime closedDateTime; //모집종료시간
    private LocalDateTime recruitingUpdateDateTime; //제한시간(너무 자주 열고닫지 않게끔 인원모집을)

    private boolean recruiting; //현재 인원 모집중인지
    private boolean published; //공개했는지 안했는지
    private boolean closed;
    private boolean useBanner;

    //******뷰에서 Spring Expression으로 study.isJoinable 이런식으로 메소드 바로 호출 가능.

    public void addManager(Account account) {
        this.managers.add(account);
        //private Set<Account> managers = new HashSet<>(); -> 위에서 new로 초기화 해놔서 getter 수정 안써도 됌.
    }
    public boolean isJoinalbe(UserAccount userAccount) { //principal정보 -userAccount에는 account정보를 꺼낼 수 있음 = 결국 account접근가능
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }
}

