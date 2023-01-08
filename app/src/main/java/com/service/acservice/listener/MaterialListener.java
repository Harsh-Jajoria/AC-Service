package com.service.acservice.listener;

import com.service.acservice.model.request.Material;

public interface MaterialListener {
    void onDeleteClicked(Material material, int position);
}
