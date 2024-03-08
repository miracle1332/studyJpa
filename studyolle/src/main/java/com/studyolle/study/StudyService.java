package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.study.event.StudyCreatedEvent;
import com.studyolle.study.event.StudyUpdateEvent;
import com.studyolle.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional //데이터를 변경하는 작업은 서비스에 위임해서 트랜잭션 안에서 처리기로 약속했기에 트랜잭셔널리드온리는 리파지토리쪽에만 줌.
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }
    public void publish(Study study) { //스터디 엔티티에서 로직이 한번 더 생기는 이유?
        study.publish();
        this.eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }
    public Study getStudyToUpdate(Account account, String path) { //스터디가 있는지 없는지
        Study study = this.getStudy(path);
        checkIfManger(study, account);
        return study;
    }
    public Study getStudy(String path) {
        Study study = this.studyRepository.findByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }
    public Study getStudyToUpdateTag(Account account, String path) { //관심주제 수정위해 스터디 가져오기
        Study study = studyRepository.findStudyWithTagsByPath(path);
        checkIfExistingStudy(path, study); //스터디 존재하는지
        checkIfManger(study, account); //매니저만 스터디의 관심주제 수정할 수 있으므로
        return study;
    }
    public Study getStudyToUpdateZone(Account account, String path) {
            Study study = studyRepository.findStudyWithZoneByPath(path);
            checkIfExistingStudy(path, study);
            checkIfManger(study, account);
            return study;
        }

    public Study getStudyToUpdateStatus(Account account, String path) { //스터디 상태변경
            Study study = studyRepository.findStudyWithManagersByPath(path);
            checkIfExistingStudy(path, study);
            checkIfManger(study, account);
            return study;
    }

    public void checkIfManger(Study study, Account account) {
        if(!study.isManagedBy(account)) {
            throw new IllegalArgumentException("해당 기능사용 권한이 없습니다.");
        }
    }

    public void checkIfExistingStudy(String path, Study study) {
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 소개를 수정하였씁니다."));
    }

    public void updateStudyImage(Study study, String image) {
        study.setImage(image);

    }

    public void enableStudyBanner(Study study) {
        study.setUseBanner(true);
    }

    public void disableStudyBanner(Study study) {
        study.setUseBanner(false);
    }


    public void addTag(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }



}
