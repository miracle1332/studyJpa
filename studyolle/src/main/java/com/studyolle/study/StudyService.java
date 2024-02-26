package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional //데이터를 변경하는 작업은 서비스에 위임해서 트랜잭션 안에서 처리기로 약속했기에 트랜잭셔널리드온리는 리파지토리쪽에만 줌.
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return null;
    }
}
