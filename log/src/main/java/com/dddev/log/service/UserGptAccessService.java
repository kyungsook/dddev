package com.dddev.log.service;

import com.dddev.log.entity.UserGptAccess;
import com.dddev.log.entity.UserLogAccess;
import com.dddev.log.exception.UserUnAuthException;
import com.dddev.log.exception.UserUnAuthGptException;
import com.dddev.log.repository.UserGptAccessRepository;
import com.dddev.log.repository.UserLogAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGptAccessService {

    private final UserGptAccessRepository userGptAccessRepository;

    //GPT 요청에 따른 COUNT 증가
    public void count(String ground_id){
        Optional<UserGptAccess> temp = userGptAccessRepository.findById(ground_id);
        //만약 groupt_id가 존재하지 않으면 redis에 생성
        if(!temp.isPresent()){
            userGptAccessRepository.save( UserGptAccess.builder()
                                                                    .groundId(ground_id)
                                                                    .count(1)
                                                                    .expiration(3600L)
                                                                    .build());
            return;
        }
        //만약 group_id가 존재하면 1분당 로그 수 + 1
        UserGptAccess userGptAccess = temp.get();
        userGptAccess.increase();
        //1시간에 60번 이상 GPT 요청 불가!
        if(userGptAccess.getCount() >= 60) {
            throw new UserUnAuthGptException("1시간에 60번만 GPT 요청이 가능합니다. 첫 시도 후 1시간 후 다시 시도하세요.");
        }
        userGptAccessRepository.save(userGptAccess);
    }
}