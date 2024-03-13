package com.studyolle.modules.event;

import com.studyolle.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "Enrollment.withEventAndStudy",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "study")
        },
        subgraphs = @NamedSubgraph(name = "study",attributeNodes = @NamedAttributeNode("study"))
) /*Enrollment.withEventAndStudy 엔티티 그래프는 Enrollment 엔티티와 그와 연관된 Event 엔티티와 Study 엔티티를 함께 로딩하는데 사용
name: 엔티티 그래프의 이름을 지정
attributeNodes: 로딩 전략을 적용할 속성 노드들을 지정합니다. 여기서 각 속성 노드는 연관 엔티티 또는 기본 타입 속성을 나타냅니다.
 subgraphs: 서브 그래프를 정의합니다. 서브 그래프는 더 깊은 수준의 로딩 전략을 지정할 때 사용됩니다.
Enrollment.withEventAndStudy 엔티티 그래프는 event 속성을 함께 로딩하고, event 엔티티와 연관된 study 속성도 함께 로딩. subgraphs 속성을 사용하여 study 속성에 대한 서브 그래프를 정의하고 있습니다. 이렇게 하면 event 엔티티와 연관된 study 속성도 함께 로딩됩니다.*/
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment { //모임 접수 엔티티

        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne //다(모임접수) 대 일(이벤트) 양방향관계 => 주인은 다 쪽, 등록은 여러개일 수 있고 알림은 하나임.
        private Event event;

        @ManyToOne //다대일 단방향
        private Account account;

        private LocalDateTime enrolledAt;

        private boolean accepted;

        private boolean attended;
}
