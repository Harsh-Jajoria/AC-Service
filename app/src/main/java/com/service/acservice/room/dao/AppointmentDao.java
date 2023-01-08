package com.service.acservice.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.service.acservice.model.response.TodayAppointmentResponse;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AppointmentDao {

  /*  @Query("Select * from AppointmentEntity")
    LiveData<List<TodayAppointmentResponse>> getAppointmentList();
*/
    @Insert
    void insertAppointment(ArrayList<TodayAppointmentResponse> appointment);
    @Update
    void updateAppointment(TodayAppointmentResponse appointment);
    @Delete
    void deleteAppointment(TodayAppointmentResponse appointment);
}
