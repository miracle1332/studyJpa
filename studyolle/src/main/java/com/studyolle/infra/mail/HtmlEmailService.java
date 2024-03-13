package com.studyolle.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

//실제로 html이메일을 보내는 구현체
@Profile("dev")
@Component @Slf4j
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService{

   private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8"); //첨부파일 보낼거면 false아니고true로 바꿔야함.
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), true); //두번째 파라미터에 html이면 true를 주면됌.
            javaMailSender.send(mimeMessage);
            log.info("sent email:{}", emailMessage.getMessage());
        }catch (MessagingException e) {
            log.error("failed to send email",e);
            throw new RuntimeException(e);
        }

    }
}
