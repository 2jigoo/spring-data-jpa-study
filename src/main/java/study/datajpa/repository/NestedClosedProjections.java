package study.datajpa.repository;

public interface NestedClosedProjections {
    
    String getUsername(); // member.username만 들고옴.
    TeamInfo getTeam(); // 최적화가 안 됨. 엔티티 전체를 불러옴. LEFT OUTER JOIN.

    interface TeamInfo {
        String getName();
    }

}
