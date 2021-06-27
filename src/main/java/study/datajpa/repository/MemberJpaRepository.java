package study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import study.datajpa.entity.Member;

// @Repository // 없어도 된다. ComponentScan뿐만 아니라 JPA의 예외를 스프링에서 공통적으로 처리할 수 있는 기능까지 포함됨.
public class MemberJpaRepository {
    
    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    
    public List<Member> findByUsername(String username) {
        // NamedQuery 호출
        List<Member> list = em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
        return list;
    }


    // Paging
    public List<Member> findByPage(int age, int offset, int limit) {
        List<Member> resultList = em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
        return resultList;
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }


    public int bulkAddAge(int age) {
        int resultCount = em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
                
        return resultCount;
    }


}
