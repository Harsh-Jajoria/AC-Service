package com.service.acservice.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import com.service.acservice.model.response.TodayAppointmentResponse;
import com.service.acservice.room.dao.AppointmentDao;

@Database(entities = TodayAppointmentResponse.class,exportSchema = false,version = 1)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    private static final String DatabaseName = "AcServiceDatabase";
    private static AppDatabase instance;
    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,DatabaseName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    public abstract AppointmentDao appointmentDao();


}
