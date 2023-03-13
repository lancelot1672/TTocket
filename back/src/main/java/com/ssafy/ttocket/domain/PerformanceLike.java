package com.ssafy.ttocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class PerformanceLike implements Serializable {
    @EmbeddedId
    private LikeId likeId;


    @ManyToOne(optional = false)
    @JoinColumn(name="performance_id")
    @MapsId("performanceId")
    private Performance performance;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id")
    @MapsId("userId")
    private User user;

    @ColumnDefault("0")
    @Column(name="is_like")
    private boolean isLike;

}
