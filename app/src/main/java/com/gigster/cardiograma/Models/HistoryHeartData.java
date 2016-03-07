package com.gigster.cardiograma.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by admin1 on 3/2/2016.
 */
@Table(name = "HistoricalData")

public class HistoryHeartData extends Model {
    @Column(name = "data")
    public String data;
    @Column(name = "heart_beat")
    public int heart_beat;
    @Column(name = "motion_state")
    public String motion_state;
    @Column(name = "saved_date")
    public Date saved_date;

    @Column(name = "motion_state_note")
    public String motion_state_note;

    @Column(name = "stringdata1")
    public String stringdata1;

    @Column(name = "stringdata2")
    public String stringdata2;

    @Column(name = "stringdata3")
    public String stringdata3;

    @Column(name = "stringdata4")
    public String stringdata4;

    @Column(name = "stringdata5")
    public String stringdata5;

    public HistoryHeartData() {
        super();
    }

    public HistoryHeartData(String data, int heart_beat, String motion_state, Date saved_date, String motion_state_note) {
        super();
        this.data = data;
        this.heart_beat = heart_beat;
        this.motion_state = motion_state;
        this.saved_date = saved_date;
        this.motion_state_note = motion_state_note;
    }


}
