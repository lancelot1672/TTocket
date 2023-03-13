package com.ssafy.ttocket.domain;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="performance_id")
    private int id;

    @NotNull
    @Column(name="performance_title")
    private String title;

    @Column(name="performance_desc")
    private String description;

    @NotNull
    @Column(name="performance_max_seats")
    private int max_seats;

    @Column(name="performance_location")
    private String location;

    @NotNull
    @Column(name="performance_price")
    private double price; // int 값 인지 물어봐야함

    @NotNull
    @Column(name="performance_start_time")
    private LocalDate startTime;

    @NotNull
    @Column(name="performance_end_time")
    private LocalDate endTime;

    @NotNull
    @Column(name="performance_poster")
    private String poster;

    @Column(name="performance_etc")
    private String etc;


}
