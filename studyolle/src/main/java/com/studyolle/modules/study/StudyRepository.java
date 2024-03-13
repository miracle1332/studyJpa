package com.studyolle.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);
    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);
    @EntityGraph(attributePaths = {"tags", "managers"})
    Study findStudyWithTagsByPath(String path);
    @EntityGraph(attributePaths = {"zones", "managers"})
    Study findStudyWithZoneByPath(String path);
    @EntityGraph(attributePaths = "managers")
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Study findStudyWithMembersByPath(String path);

    Study findStudyOnlyByPath(String path); //엔티티 어노테이션 없기때문에 onlyby들어간것?
}
