package com.studyolle.modules.study;

import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.account.UserAccount;
import com.studyolle.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime; //공개한 시간
    private LocalDateTime closedDateTime; //모집종료시간
    private LocalDateTime recruitingUpdateDateTime; //제한시간(너무 자주 열고닫지 않게끔 인원모집을)

    private boolean recruiting; //현재 인원 모집중인지
    private boolean published; //공개했는지 안했는지
    private boolean closed;
    private boolean useBanner;
    private int memberCount;

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

    public boolean isManagedBy(Account account) { return this.getManagers().contains(account);}

    public String getEncodePath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

    public void publish() {
        if(!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        }else {
            throw new RuntimeException("스터디를 공개할 수 없는 상태입니다. 이미 공개했거나 종료되었을 수 있습니다. ");
        }
    }

    public void close() {
        if(this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        }else  {
            throw new RuntimeException("스터디를 종료할 수 없습니다. 공개된 스터디가 아니거나 이미 종료된 스터디입니다.");
        }
    }

    public boolean canUpdateRecruiting() { //인원 모집 가능 여부
        return this.published && this.recruitingUpdateDateTime == null
                || this.recruitingUpdateDateTime.isBefore(LocalDateTime.now().minusHours(1)); //제한시간 1시간
    }

    public void startRecruit() {
        if(canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdateDateTime = LocalDateTime.now();
        }else {
            throw new RuntimeException("인원모집을 시작할 수 없습니다. 스터디를 공개하거나 한시간 뒤에 다시 시도해주세요.");
        }
    }

    public void stopRecruit() { //스터디 엔티티의 boolean 타입 recruiting 의 상태(t,f)를 바꿔주는 메소드 ->
        if(canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdateDateTime = LocalDateTime.now();
        }else  {
            throw new RuntimeException("인원모집을 중지할 수 없습니다. 스터디 공개 혹은 한시간뒤 다시 시도바람");
        }
    }

    public boolean isRemovable() {
        return !this.published; //TODO 모임을 했던 스터디는 삭제할 수 없다.
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
        this.memberCount++;
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
        this.memberCount--;
    }
}

