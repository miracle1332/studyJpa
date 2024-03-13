package com.studyolle.modules.event;

import com.studyolle.modules.study.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyOrderByStartDateTime(Study study); //Study 엔티티와 관련된 Event 엔티티를 조회하고, 조회된 결과를 시작 일시(startDateTime)에 따라 정렬하여 반환

}
