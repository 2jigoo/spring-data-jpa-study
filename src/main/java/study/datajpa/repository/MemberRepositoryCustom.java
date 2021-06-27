package study.datajpa.repository;

import java.util.List;

import study.datajpa.entity.Member;

public interface MemberRepositoryCustom {
    
    /*
        커맨드 / 쿼리
        핵심 비지니스 로직 / 뷰 로직
        라이프 사이클에 따라 변경해야하는 것들
    */

    List<Member> findMemberCustom();
    
}
