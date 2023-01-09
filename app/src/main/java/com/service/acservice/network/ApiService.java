package com.service.acservice.network;

import com.service.acservice.model.request.AppointmentDetailsRequest;
import com.service.acservice.model.request.CommonRequest;
import com.service.acservice.model.request.EditDetailsRequest;
import com.service.acservice.model.request.LoginRequest;
import com.service.acservice.model.request.StepFiveRequest;
import com.service.acservice.model.request.StepFourRequest;
import com.service.acservice.model.request.StepOneRequest;
import com.service.acservice.model.request.StepThreeRequest;
import com.service.acservice.model.request.StepTwoRequest;
import com.service.acservice.model.request.TodayAppointmentRequest;
import com.service.acservice.model.response.AppointmentDetailsResponse;
import com.service.acservice.model.response.BrandsResponse;
import com.service.acservice.model.response.CallTypeResponse;
import com.service.acservice.model.response.CategoryResponse;
import com.service.acservice.model.response.CommonResponse;
import com.service.acservice.model.response.DashboardResponse;
import com.service.acservice.model.response.DropDownResponse;
import com.service.acservice.model.response.LoginResponse;
import com.service.acservice.model.response.MaterialResponse;
import com.service.acservice.model.response.PurchaseFromResponse;
import com.service.acservice.model.response.PurchaseTypeResponse;
import com.service.acservice.model.response.StatusResponse;
import com.service.acservice.model.response.SubStatusResponse;
import com.service.acservice.model.response.TodayAppointmentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("appointments")
    Call<DashboardResponse> dashboard(@Body CommonRequest commonRequest);

    @POST("appointment-listing")
    Call<TodayAppointmentResponse> todayAppointment(@Body TodayAppointmentRequest todayAppointmentRequest);

    @POST("appointment-detail")
    Call<AppointmentDetailsResponse> appointmentDetails(@Body AppointmentDetailsRequest appointmentDetailsRequest);

    @GET("brand")
    Call<BrandsResponse> getBrands();

    @GET("material")
    Call<MaterialResponse> getMaterial();

    @GET("calltype")
    Call<CallTypeResponse> getCallType();

    @POST("request-edit")
    Call<CommonResponse> editDetails(@Body EditDetailsRequest editDetailsRequest);

    @GET("purchasedfromType")
    Call<PurchaseTypeResponse> getPurchaseType();

    @GET("purchasedfrom")
    Call<PurchaseFromResponse> getPurchaseFrom();

    @GET("product_category")
    Call<CategoryResponse> getCategory();

    @GET("status")
    Call<StatusResponse> getStatus();

    @GET("substatus")
    Call<SubStatusResponse> getSubStatus();

    @GET("dropdownlist")
    Call<DropDownResponse> getDropDown();

    @POST("step1")
    Call<CommonResponse> submitStepOne(@Body StepOneRequest stepOneRequest);

    @POST("step2")
    Call<CommonResponse> submitStepTwo(@Body StepTwoRequest stepTwoRequest);

    @POST("step3")
    Call<CommonResponse> submitStepThree(@Body StepThreeRequest stepThreeRequest);

    @POST("step4")
    Call<CommonResponse> submitStepFour(@Body StepFourRequest stepFourRequest);

    @POST("step5")
    Call<CommonResponse> submitStepFive(@Body StepFiveRequest stepFiveRequest);

}
