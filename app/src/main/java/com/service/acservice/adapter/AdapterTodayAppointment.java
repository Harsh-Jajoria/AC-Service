package com.service.acservice.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.acservice.databinding.ItemAppointmentBinding;
import com.service.acservice.listener.TodayAppointmentListener;
import com.service.acservice.model.response.TodayAppointmentResponse;

import java.util.List;

public class AdapterTodayAppointment extends RecyclerView.Adapter<AdapterTodayAppointment.TodayAppointmentViewHolder>{
    List<TodayAppointmentResponse.Datum> todayAppointmentList;
    TodayAppointmentListener todayAppointmentListener;

    public AdapterTodayAppointment(List<TodayAppointmentResponse.Datum> todayAppointmentList, TodayAppointmentListener todayAppointmentListener) {
        this.todayAppointmentList = todayAppointmentList;
        this.todayAppointmentListener = todayAppointmentListener;
    }

    @NonNull
    @Override
    public TodayAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodayAppointmentViewHolder(
                ItemAppointmentBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TodayAppointmentViewHolder holder, int position) {
        holder.bindData(todayAppointmentList.get(position));
    }

    @Override
    public int getItemCount() {
        return todayAppointmentList.size();
    }

    public void filterList( List<TodayAppointmentResponse.Datum> filterList) {
        todayAppointmentList = filterList;
        notifyDataSetChanged();
    }

    class TodayAppointmentViewHolder extends RecyclerView.ViewHolder {
        ItemAppointmentBinding binding;

        TodayAppointmentViewHolder(ItemAppointmentBinding itemAppointmentBinding) {
            super(itemAppointmentBinding.getRoot());
            binding = itemAppointmentBinding;
        }
        void bindData(TodayAppointmentResponse.Datum todayList) {
            binding.tvCustomerName.setText(String.format("%s : %s", "Name", todayList.getAccount()));
            binding.tvCustomerAddress.setText(String.format("%s : %s", "Address", todayList.getAddress()));
            binding.tvContactNumber.setText(String.format("%s : %s", "Contact No.", todayList.getRegistered_phone()));
            binding.tvStatus.setText(String.format("%s : %s", "Status" , todayList.getStatus()));
            binding.tvProblem.setText(String.format("%s : %s", "Case" , ""));
            binding.getRoot().setOnClickListener(v -> todayAppointmentListener.onAppointmentClick(todayList));
        }

    }
}
