package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
@Getter
public class UserAccount extends User { //user정보는 스프링시큐리티에서 오는것
    //UserAccount를 principal객체로 사용할 예정
    //스프링시큐리티가ㅏ 다루는 유저정보를 우리가 가지고 있는 유저정보와 연동을 해주는 것
    //USERaccount라고 스프링시큐리티가 다루는 유저정보와 우리 도메인에서 다루는 유저정보에 그 사이에 갭을 매꿔주는 일종의 어댑터라고 생각하면 된다
    private Account account;
    public UserAccount(Account account){
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
