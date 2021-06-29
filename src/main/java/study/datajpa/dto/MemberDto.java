package study.datajpa.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import study.datajpa.entity.Member;

@Getter @Setter
@ToString
public class MemberDto {
    
    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        // this.teamName = member.getTeam().getName();
    }
    
}
