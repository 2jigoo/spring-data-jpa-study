package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    
    @Value("#{target.username + ' ' + target.age}")
    String getUsername(); // opend Projection. 엔티티를 다 들고와서 처리
    
    // String getUsername(); // closed Proejction 정확하게 일치하는 것만 들고옴
}
