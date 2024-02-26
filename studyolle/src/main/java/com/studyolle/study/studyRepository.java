package com.studyolle.study;

import com.studyolle.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface studyRepository extends JpaRepository<Study, Long> {
}
