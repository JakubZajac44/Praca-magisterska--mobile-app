package com.example.jakub.arapp.page.settingsPage;

import dagger.Binds;
import dagger.Module;

public interface SettingsContract {

    interface View{

    }

    interface Presenter{
        void attachView(SettingsContract.View view);
        void detachView();
    }



    @Module()
    abstract class SettingsModule {

        @Binds
        public abstract SettingsContract.Presenter provideSettingsPresenter (SettingsPresenter settingsPresenter);
    }
}
