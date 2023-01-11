package com.service.acservice.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.service.acservice.fragment.AdditionalMaterialFragment;
import com.service.acservice.fragment.ImageSelectionFragment;
import com.service.acservice.fragment.PersonalInformationFragment;
import com.service.acservice.fragment.ProductInformationFragment;
import com.service.acservice.fragment.PurchaseInformationFragment;
import com.service.acservice.fragment.RemarksFragment;

public class AdapterDetailsFragment extends FragmentStateAdapter {
    String id;

    public AdapterDetailsFragment(@NonNull FragmentActivity fragmentActivity, String id) {
        super(fragmentActivity);
        this.id = id;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ProductInformationFragment(id);
            case 2:
                return new PurchaseInformationFragment(id);
            case 3:
                return new AdditionalMaterialFragment(id);
            case 4:
                return new ImageSelectionFragment(id);
            case 5:
                return new RemarksFragment(id);
            default:
                return new PersonalInformationFragment(id);
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
