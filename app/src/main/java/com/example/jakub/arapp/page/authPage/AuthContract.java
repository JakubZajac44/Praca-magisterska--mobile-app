package com.example.jakub.arapp.page.authPage;

import dagger.Binds;
import dagger.Module;

public interface AuthContract {

    interface View {

    }

    interface Presenter {
        void attachView(View view);

        void detachView();
    }

    @Module()
    abstract class AuthModule {

        @Binds
        public abstract AuthContract.Presenter provideAuthPresenter (AuthPresenter authPresenter);
    }
}
