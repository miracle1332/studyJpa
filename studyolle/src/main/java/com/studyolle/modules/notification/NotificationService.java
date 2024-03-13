package com.studyolle.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void markAsRead(List<Notification> notifications) { //알림읽음 표시
        notifications.forEach(n -> n.setChecked(true)); //알림리스트 각각을 체크하고
        notificationRepository.saveAll(notifications); //알림리스트를 리파지토리에 모두 저장
    }
}
