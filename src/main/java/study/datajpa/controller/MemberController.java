package study.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;


@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberRepository memberRepository;

    @GetMapping(value="/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 단순 조회용. 트랜잭션이 없는 범위에서 엔티티를 조회했기 때문에 DB에 반영되지 않는다
    @GetMapping(value="/members_converter/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }


    // @Qualifier: 페이징 정보가 둘 이상일 때 접두사로 구분해준다
    // @Qualifier("member") Pageable memberPageable, ...

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        // Page<Member> page = memberRepository.findAll(pageable);
        // Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        
        Page<MemberDto> page = memberRepository.findAll(pageable).map(MemberDto::new);
        return page;
    }

    // 엔티티를 그대로 반환하면 안된다. 내부 설계를 노출하는 것과 같음


    @PostConstruct
    public void init() {
        for(int i = 0; i < 40; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
    
}
