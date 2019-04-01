package com.example.jakub.arapp.notification;

import android.app.Notification;

import dagger.Binds;
import dagger.Module;

public interface NotificationProvider {

    Notification getNotificationProvider();

    @Module()
    abstract class NotificationProviderModule {

        @Binds
        public abstract NotificationProvider provideNotoficationProvider (NotificationProviderImpl notificationProvider);
    }
}
