package com.example.jakub.arapp.motionSensor.gyroFilter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {GyroscopeFilterProvider.GyroscopeFilterModule.class})
public interface GyroComponent {
}
