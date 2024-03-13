package com.studyolle.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data //회원가입할때 받아올 데이타들
public class SignUpForm {


    @NotBlank //비어있는 값이면 안되고
    @Length(min = 3, max = 20) //길이설정
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$") //닉네임으로 쓸 수 있는 패턴을 정규식으로 정의할수 있음
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;
}
