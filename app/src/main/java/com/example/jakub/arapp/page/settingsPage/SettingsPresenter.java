package com.example.jakub.arapp.page.settingsPage;

import javax.inject.Inject;

public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View view;

    @Inject
    public SettingsPresenter() {
    }


    @Override
    public void attachView(SettingsContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
