package com.ssafy.ttocket.domain;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User {

    @Id
    @Column(name="user_id")
    private String id;

    @NotNull
    @Column(name="user_nickname")
    private String nickname;
}
