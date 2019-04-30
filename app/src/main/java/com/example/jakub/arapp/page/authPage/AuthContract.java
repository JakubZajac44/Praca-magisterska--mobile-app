package com.example.jakub.arapp.page.authPage;

import com.example.jakub.arapp.model.User;

import dagger.Binds;
import dagger.Module;

public interface AuthContract {

    interface View{
        void showToast(String string);
    }

    interface LoginView extends View{


        void FingerprintDialogFragmentClosed(boolean authStatus);

        void userLogged();

//        void userLogged();
    }

    interface ReLoginView extends View{

        void userReLogged(User user);
    }

    interface RegisterView  extends View{
        void userRegistered();
    }

    interface Presenter {
        void attachView(View view);

        void detachView();

        void registerUser(String name, String password);

        void loginUserPin(String pin);


        void saveUserCredentials(User user);

        void savePin(String pin);

        void loginUserFingerPrint();

        void reLoginUser(String name, String password);
    }

    @Module()
    abstract class AuthModule {

        @Binds
        public abstract AuthContract.Presenter provideAuthPresenter (AuthPresenter authPresenter);
    }
}
