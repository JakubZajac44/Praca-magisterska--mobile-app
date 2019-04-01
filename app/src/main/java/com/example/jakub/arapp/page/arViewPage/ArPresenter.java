package com.example.jakub.arapp.page.arViewPage;

import javax.inject.Inject;

public class ArPresenter implements ArContract.Presenter {

    ArContract.View view;

    @Inject
    public ArPresenter() {

    }


    @Override
    public void attachView(ArContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
