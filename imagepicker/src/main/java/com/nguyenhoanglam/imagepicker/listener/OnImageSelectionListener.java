package com.nguyenhoanglam.imagepicker.listener;

import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.List;



public interface OnImageSelectionListener {
    void onSelectionUpdate(List<Image> images);
}
