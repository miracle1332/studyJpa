package com.studyolle.zone;

import com.studyolle.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository zoneRepository;


    @PostConstruct //빈이 만들어진 이후 실행이 되는 지점. 빈이초기화 된 후 이쪽에 있는 코드블럭이 실행됌.
    public void initZoneData() throws IOException {
        if(zoneRepository.count() == 0) { //존리파지토리에 데어터가 하나도 없으면 그떄 넣게끔
            Resource resource = new ClassPathResource("zones_kr.csv");
             List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    //각각의 라인을 한줄씩 읽어들임 -> 한 줄을 zone이라는 객체로 변환
                    .map(line -> {
                        String[] split =line.split(","); //콧마 기준으로 쪼갬
                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                        //존객체 생성
                    }).collect(Collectors.toList()); //한 줄 씩 객체 변환한것을 마지막에 collect해서 listfh qkedma
            zoneRepository.saveAll(zoneList); //뭉텅이로 db에 저장
        } ///-> 이 정보를 태기파이에서 whiteList로 쓰는거고 이 정보만 쓰는것! 새로운 지역정보 넣는건 안됌.
    }
}
