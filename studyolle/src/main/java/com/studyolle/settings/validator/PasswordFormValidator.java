package com.studyolle.settings.validator;


import com.studyolle.settings.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
//Bean으로 등록하지 않음. 왜냐면 이 패스워드 벨리데이터는 다른 빈을 사용할 게 없으므로 이건 그냥 new생성하면 됌.
public class PasswordFormValidator implements Validator {
    @Override //어떤 타입의 폼 객체를 검증할 것이냐
    public boolean supports(Class<?> aClass) {
        return PasswordForm.class.isAssignableFrom(aClass);
        //passwordForm타입에 할당가능한 맞는 타입이면 검증을 하겠다는것.
    }

    @Override
    public void validate(Object target, Errors errors) {
    //타겟 객체는 passwordForm.
        PasswordForm passwordForm = (PasswordForm)target;
        if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
           errors.rejectValue("newPassword","worng.value","입력한 새 패스워드가 일치하지 않음");
        }
    }
}
