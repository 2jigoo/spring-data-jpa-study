package study.datajpa.repository;


import java.util.Collection;
import java.util.List;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;


// 인터페이스이므로 여러 개의 인터페이스 상속 가능
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {
    
    // List<Member> findByUsername(String username);

    // 메서드쿼리
    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    public List<Member> findTop3HelloBy(); // 조건이 없으면 전체 조회


    // @Query(name = "Member.findByUsername") // 없어도 동작은 잘 됨.
    // (1) "도메인클래스명.메서드이름"으로 NamedQuery를 찾는다
    // (2) 없으면 메서드 이름 쿼리
    // 엔티티 클래스가 복잡해지기 때문에 권장하지는 않지만, 어플리케이션 로딩 시점에 파싱될 때 에러를 찾을 수 있다는 장점이 있다.
    List<Member> findByUsername(@Param("username") String username);


    // 이름이 없는 NamedQuery 느낌. 애플리케이션 로딩 시점에서 에러 잡을 수 있음.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username,  @Param("age") int age);


    @Query("select m.username from Member m")
    List<String> findUsernameList();


    // DTO로 조회 
    // JPQL이 제공하는 오퍼레이션
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();


    /**
     * 파라미터 바인딩
     * (1) !!! 이름 기반 !!!
     *      select m from Member m where m.username = :username
     * (2) 위치 기반
     *      select m from Member m where m.username = ?0
     *  */ 

    
    // in절
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);


    /*
    // 반환 타입. 결과가 없으면?
    Member findMemberByUsername(String username); // null
    List<Member> findListByUsername(String username); // emptyList. (null 체크 불가능. size())
    Optional<Member> findOptionalByUsername(String username);
    */



    /**
     * 페이징
     * 
     * - 카운터 쿼리 분리할 수 있다. (기본 쿼리를 쓰면 필요 없는 조인까지 수행한다)
     * - Sort도 복잡해지는 경우 쿼리 직접 쓰는 게 좋은 경우도 있다.
     */

    
    /* @Query(value = "select m from Member m left join m.team t",
        countQuery = "select count(m.username) from Member m") */
    Page<Member> findByAge(int age, Pageable pageable);
    // Slice<Member> findByAge(int age, Pageable pageable);


    /**
     * @Modifying. 벌크 연산 excuteUpdate() 호출
     * 영속성 컨텍스트를 무시하고 쿼리 날림.
     * JPQL은 쿼리 보내고 flush() 한다.
     */
    @Modifying(clearAutomatically = true) // 영속성 컨텍스트 clear
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAddAge(@Param("age") int age);

    // Jdbc, JdbcTemplate, Mybatis로 쿼리 실행한 건 JPA가 인식하지 못한다. flush(), clear() 필요


    /**
     * Fetch 조인
     * - 지연로딩이 적용되어 있어도 한번에 받아옴.
     */
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();


    /**
     * EntityGraph
     */

    // JPQL 없이 fetch 조인
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") // namedEntityGraph 지정
    List<Member> findEntityGraphByUsername(@Param("username") String username);



    /**
     * JPA Hint 와 Lock
     */

    // 아주 중요하고 트래픽이 많은 API에서 한정적으로 적용하는 것을 고려... 성능 테스트를 해서 이점이 클 때만...
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // select for update 기능... optimistic lock 버저닝 메커니즘으로 락...
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    
    // Projections
    //  List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);


    // NativeQuery
    // return type: Objec[], Tuple, DTO
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
     
}
