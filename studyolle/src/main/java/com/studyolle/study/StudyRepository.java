package com.studyolle.study;

import com.studyolle.domain.Study;
import com.studyolle.study.form.StudyForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(StudyForm studyForm);

    Study findByPath(String path);
}
