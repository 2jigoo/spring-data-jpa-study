package study.datajpa.entity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import study.datajpa.repository.MemberRepository;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("taemA");
        Team teamB = new Team("taemB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10 ,teamA);
        Member member2 = new Member("member2", 20 ,teamA);
        Member member3 = new Member("member3", 30 ,teamA);
        Member member4 = new Member("member4", 40 ,teamA);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }


    @Test
    public void JpaEventBaseEntity() throws Exception {
        // given
        Member member = new Member("member");
        memberRepository.save(member); // @PrePersist

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); // @PreUpdate
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        System.out.println("createdDate: " + findMember.getCreatedDate());
        System.out.println("lastModifiedDate: " + findMember.getLastModifiedDate()); // 100 ms 차이
        System.out.println("createdBy: " + findMember.getCreatedBy());
        System.out.println("lastModifiedBy: " + findMember.getLastModifiedBy());

    }

}
