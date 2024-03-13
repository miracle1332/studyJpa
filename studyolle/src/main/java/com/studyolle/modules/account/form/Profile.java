package com.studyolle.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
//@NoArgsConstructor
public class Profile {

    private String profileImage; // 프로필로, 폼을 채울 객체

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private  String location;

}
