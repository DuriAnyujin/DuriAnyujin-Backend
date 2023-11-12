package com.durianyujin.app.couple.member.infrastructure;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "char(10)")
    private String name;

    @Column(columnDefinition = "char(4)")
    private String nickname;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String code;

    @Column(name ="is_connected", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isConnected;

    @Column(name = "social_id", nullable = false, columnDefinition = "VARCHAR(30)")
    private String socialId;

    @Column(name = "social_type", nullable = false, columnDefinition = "VARCHAR(10)")
    private String socialType;

    @Column(name = "refresh_token", nullable = false, columnDefinition = "VARCHAR(30)")
    private String refreshToken;

    @Builder
    public Member(String name, String nickname, String code, Boolean isConnected, String socialId, String socialType, String refreshToken) {
        this.name = name;
        this.nickname = nickname;
        this.code = code;
        this.isConnected = isConnected;
        this.socialId = socialId;
        this.socialType = socialType;
        this.refreshToken = refreshToken;
    }

}
