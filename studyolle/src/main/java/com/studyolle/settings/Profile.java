package com.studyolle.settings;

import com.studyolle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class Profile { // 프로필로, 폼을 채울 객체

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    private String occupation;

    private  String location;

    //폼을 채울때 어카운트 정보를 이용해서 채워야하기에
    public Profile(Account account) {
        this.bio = account.getBio();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
