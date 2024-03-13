package com.studyolle.modules.event;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.event.event.EnrollmentAcceptedEvent;
import com.studyolle.modules.event.event.EnrollmentRejectedEvent;
import com.studyolle.modules.event.form.EventForm;
import com.studyolle.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Event createEvent(Event event, Study study, Account account) { //알림생성 메소드
        event.setCreatBy(account); //누가만들었는지
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), "'" + event.getTitle() + "'모임을 만들었습니다."));
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) { //알림 수정 메소드
        modelMapper.map(eventForm, event); //모델매퍼 사용하여 이벤트
        event.acceptWaitingList();

    }
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), "'" +
                event.getTitle()+"'모임을 취소했습니다. "));
    }

    public void newEnrollment(Event event, Account account) { //새등록
        if(!enrollmentRepository.existsByEventAndAccount(event, account)){ //리파지토리에서 이벤트와 사용자계정 있는지 확인하고
            Enrollment enrollment = new Enrollment(); //****왜 생성자주입 안받지??? -> 등록객체는 여러개의 인스턴스이여야 하기 떄문에?
            enrollment.setEnrolledAt(LocalDateTime.now()); //등록객체에에 등록시간, 승인여부, 계정 넣어주고
            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment()); //erollment객체에서 accepted는 boolean타입.
            enrollment.setAccount(account);
            event.addEnrollment(enrollment); //이벤트에서 등록 넣어주고
            enrollmentRepository.save(enrollment); //등록리파지토리에서 지금 등록정보 저장
        }
    }

    public void cancelEnrollment(Event event, Account account) { //사용자의 접수 취소
       Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
       if(!enrollment.isAttended()) {
           event.removeEnrollment(enrollment);
           enrollmentRepository.delete(enrollment);
           event.acceptNextWaitingEnrollment();
       }

    }

    //****이벤트 퍼블리셔
    public void acceptEnrollment(Event event, Enrollment enrollment) { //관리자의 접수 승인
        event.accept(enrollment); //트랜지셔널때문에 컨트롤러에서 바로 도메인 메소드 안부르는것
        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment)); //왜 상속클래스 만들었느지 모르겠다.
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
    }

    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(false);
    }
}
