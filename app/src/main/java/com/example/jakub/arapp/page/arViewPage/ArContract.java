package com.example.jakub.arapp.page.arViewPage;

import com.example.jakub.arapp.motionSensor.sensorUtility.Orientation3d;

import dagger.Binds;
import dagger.Module;

public interface ArContract {

    interface View {

        boolean isVisible();
        void orientationChanged(Orientation3d newOrientation);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
    }

    @Module()
    abstract class LoginModule {

        @Binds
        public abstract ArContract.Presenter provideLoginPresenter (ArPresenter loginPresenter);
    }
}
