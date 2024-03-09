package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.event.form.EventForm;
import com.studyolle.study.event.StudyUpdateEvent;
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
}
