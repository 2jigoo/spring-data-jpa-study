package study.datajpa.repository;

public class UsernameOnlyDto {
    
    private final String username;

    // 생성자의 파라미터명이 중요
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
