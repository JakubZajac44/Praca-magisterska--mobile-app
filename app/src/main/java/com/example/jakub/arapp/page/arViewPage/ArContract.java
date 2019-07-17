package com.example.jakub.arapp.page.arViewPage;

import com.example.jakub.arapp.motionSensor.sensorUtility.Orientation3d;

import dagger.Binds;
import dagger.Module;

public interface ArContract {

    interface View {

        boolean isVisible();
        void orientationChangedText(Orientation3d newOrientation);

        void detailChangedText(String name, String sample, String distance);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
    }

    @Module()
    abstract class ArModule {

        @Binds
        public abstract ArContract.Presenter provideArPresenter (ArPresenter arPresenter);
    }
}
