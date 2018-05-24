package com.a3dx2.clock.activity;

import android.content.ContentResolver;
import android.content.Context;

import com.a3dx2.clock.service.model.BrightnessContext;

public interface BrightnessAwareActivity {

    BrightnessContext getBrightnessContext();

    ContentResolver getContentResolver();

    Context getContext();

}
