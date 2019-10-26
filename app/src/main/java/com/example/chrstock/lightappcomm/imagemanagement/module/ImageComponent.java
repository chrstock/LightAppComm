package com.example.chrstock.lightappcomm.imagemanagement.module;

import com.example.chrstock.lightappcomm.MainActivity;
import com.example.chrstock.lightappcomm.imagemanagement.api.ImageProcessor;

import dagger.Component;

@Component(modules = {ImagesModule.class})
public interface ImageComponent {

    ImageProcessor provideImageprocessor();

    void inject(MainActivity activity);
}
