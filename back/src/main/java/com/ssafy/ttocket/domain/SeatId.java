package com.ssafy.ttocket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SeatId implements Serializable {
    @Column(name="seat_no")
    private int seatNo;

    @Column(name="performance_id")
    private int performanceId;
}
