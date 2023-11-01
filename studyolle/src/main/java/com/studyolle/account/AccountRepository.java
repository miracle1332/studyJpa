package com.studyolle.account;

import com.studyolle.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)//조회용 메서드 명시
public interface AccountRepository extends JpaRepository<Account, Long> {
    //이정도의 기능은 스프링데이타jpa가 만들어줌
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);
}
