package com.service.acservice.room;

import android.app.Application;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.service.acservice.activities.MainActivity;
import com.service.acservice.model.request.TodayAppointmentRequest;
import com.service.acservice.model.response.TodayAppointmentResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;
import com.service.acservice.room.dao.AppointmentDao;
import com.service.acservice.utils.Constants;
import com.service.acservice.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    Application application;
    AppointmentDao appointmentDao;
    PreferenceManager preferenceManager;

    /*
    1) Fetch the data from server.
    2) Insert into database.
     */
    public Repository(Application application) {
        this.application = application;
        this.appointmentDao = AppDatabase.getInstance(application).appointmentDao();
    }


    /*public LiveData<List<TodayAppointmentResponse>> getAppointmentList(){
        return appointmentDao.getAppointmentList();
    }*/

    public void insertAppointment(TodayAppointmentResponse appointment){
//        appointmentDao.insertAppointment(appointment);

    }
//    void updateAppointment(TodayAppointmentResponse appointment);
//    void deleteAppointment(TodayAppointmentResponse appointment);

//    public void fetchTodayAppointment(String status) {
//        preferenceManager = new PreferenceManager(application.getApplicationContext());
//
//        try {
//            TodayAppointmentRequest todayAppointmentRequest = new TodayAppointmentRequest(
//                    preferenceManager.getString(Constants.USER_ID),
//                    status
//            );
//            ApiClient.getRetrofit().create(ApiService.class)
//                    .todayAppointment(todayAppointmentRequest).enqueue(new Callback<TodayAppointmentResponse>() {
//                        @Override
//                        public void onResponse(@NonNull Call<TodayAppointmentResponse> call, @NonNull Response<TodayAppointmentResponse> response) {
//                            if (response.isSuccessful() && response.body() != null) {
//                                if (response.body().code == 200) {
//                                    appointmentDao.insertAppointment(response.body().data);
//                                    appointmentModelList.addAll(response.body().data);
//                                    adapterTodayAppointment.notifyDataSetChanged();
//                                    binding.swipeRefresh.setVisibility(View.VISIBLE);
//                                    binding.shimmer.stopShimmer();
//                                    binding.shimmer.setVisibility(View.GONE);
//                                } else {
//                                    ((MainActivity) application.getApplicationContext()).showSnackBar("No Data Found");
//                                }
//                            } else {
//                                ((MainActivity) application.getApplicationContext()).showSnackBar("Something went wrong");
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<TodayAppointmentResponse> call, @NonNull Throwable t) {
//                            ((MainActivity) application.getApplicationContext()).showSnackBar("Check your internet connection");
//                        }
//                    });
//
//        } catch (Exception e) {
//            Toast.makeText(application.getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

}
