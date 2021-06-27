package study.datajpa.repository;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

// MemberRepository(원 리포지토리 이름) + "Impl"이라고 이름해야 한다.
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    
    private final EntityManager em;


    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    
}
