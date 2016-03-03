package com.gigster.cardiograma.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

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




    public HistoryHeartData() {
        super();
    }

    public HistoryHeartData(String data, int heart_beat, String motion_state, Date saved_date) {
        super();
        this.data = data;
        this.heart_beat = heart_beat;
        this.motion_state = motion_state;
        this.saved_date = saved_date;
    }


}
