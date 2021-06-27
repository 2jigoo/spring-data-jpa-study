package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {
    
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;
    

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }
    
    
    @Test
    public void 메서드쿼리1() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);

    }


    @Test
    public void 메서드쿼리2() {
        List<Member> result = memberRepository.findTop3HelloBy();
        // no property age found

    }

    
    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);
        
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 20);
        m1.setTeam(team);
        m2.setTeam(team);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // List<MemberDto> members = memberRepository.findMemberDto();
        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        // for(MemberDto dto : members) {
        for(Member dto : members) {
            System.out.println("dto = " + dto);
        }
    }



    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); // totalCount 쿼리도 포함
        // Slice<Member> page = memberRepository.findByAge(age, pageRequest); // n+1개 조회. count X

        Page<MemberDto> toDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));


        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        content.forEach(mem -> System.out.println("member: " + mem));
        System.out.println("total: " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAddAge(20); // DB에는 41로 반영
        // em.flush(); // JPQL이 flush 해줌
        // em.clear(); // @Modifying 속성 활용

        Member findMember = memberRepository.findByUsername("member5").get(0); // clear하지 않으면 영속성 컨텍스트에는 40이다.
        System.out.println("member5: " + findMember);

        // then
        assertThat(resultCount).isEqualTo(3);

    }


    @Test
    public void findMemberLazy() {

        // given
        // member1 -> team A
        // member2 -> team B

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();


        // when (N + 1문제)

        // select Member (쿼리 1개 날렸는데...)
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member: " + member.getUsername());
            System.out.println("member's team.class: " + member.getTeam().getClass()); // class study.datajpa.entity.Team$HibernateProxy$KxIgpDqq (프록시 객체)
            System.out.println("member's team name: " + member.getTeam().getName()); // select Team. (쿼리 N개...)
        }

    }


    

    @Test
    public void queryHint() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when

        // Member findMember = memberRepository.findById(member1.getId()).get();
        // findMember.setUsername("member2");
        // 1. 변경 감지(dirty checking)로 flush 할 때 update 쿼리 나감
        // 변경 감지를 하려면 내부적으로 객체를 두 개가 필요함. 비효율적.
        
        // 2. 변경 감지가 필요 없는 경우라면? readOnly로 가져오자
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2"); // 변경 감지 체크 X. (스냅샷 없음)

        em.flush();
        
    }


    @Test
    public void lock() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");
        
    }


}
