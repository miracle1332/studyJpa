package com.studyolle.modules.notification;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentAccount;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentAccount Account account, Model model) { //알림 목록 가져오기
        List<Notification> notificationList = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);
        //-> 확인하지 않은 알림목록 가져오기
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true); //확인한 알림 목록 개수
        putCategorizedNotifications(model, notificationList, numberOfChecked, notificationList.size());

    }

    private void putCategorizedNotifications(Model model, List<Notification> notificationList, long numberOfChecked,
                                         long numberOfNotChecked) {
        //알림 카테고리화 하기
        List<Notification> newStudyNotifications = new ArrayList<>(); //새로운 스터디 아림
        List<Notification> eventEnrollmentNotifications = new ArrayList<>(); //모임신청 참가신청 결과 알림
        List<Notification> watchingStudyNotifications = new ArrayList<>(); //참여중인 스터디 알림
        for(Notification notification: notificationList) { //파라미터로 받은 알림목록을 for문 돌려서 해당하는 카테고리에 break;
            switch (notification.getNotificationType()) {
                case STUDY_CREATED: newStudyNotifications.add(notification); break;
                case EVENT_ENROLLMENT: eventEnrollmentNotifications.add(notification); break;
                case STUDY_UPDATED: watchingStudyNotifications.add(notification); break;
            }
        }

        //모델에 넘겨주기
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notificationList);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
    }

}
