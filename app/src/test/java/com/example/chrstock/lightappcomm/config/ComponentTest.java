package com.example.chrstock.lightappcomm.config;

import com.example.chrstock.lightappcomm.temp.DaggerImageComponent;
import com.example.chrstock.lightappcomm.imagemanagement.module.ImageComponent;
import com.example.chrstock.lightappcomm.imagemanagement.api.ImageProcessor;
import com.example.chrstock.lightappcomm.imagemanagement.module.ImagesModule;

import org.junit.Before;

/**
 * Abstract class for Unit tests
 */
public abstract class ComponentTest {

    private ImageProcessor imageProcessor;

    protected ImageProcessor getImageProcessor(){
        return imageProcessor;
    }

    @Before
    public void setUp(){
        ImageComponent component = DaggerImageComponent.builder().imagesModule(new ImagesModule()).build();
        imageProcessor = component.provideImageprocessor();
    }
}
