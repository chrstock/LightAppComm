package com.example.chrstock.lightappcomm.imagemanagement.module;

import com.example.chrstock.lightappcomm.imagemanagement.api.ImageProcessor;
import com.example.chrstock.lightappcomm.imagemanagement.impl.ImageProcessorImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class ImagesModule {

    @Provides
    public ImageProcessor provideImageProcessor(){
        return new ImageProcessorImpl();
    }
}
