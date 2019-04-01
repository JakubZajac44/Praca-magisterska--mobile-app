package com.example.jakub.arapp.dialogFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jakub.arapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SimpleChoiceDialog extends DialogFragment {

    public static final String TAG = SimpleChoiceDialog.class.getSimpleName();
    public static final String KEY_TITLE = "title_key";

    private Unbinder unbinder;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_dialog_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        setCancelable(false);
        return view;
    }


    @OnClick(R.id.yesSimpleDialogButton)
    public void yesClickButton(){
        dismiss();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());

    }

    @OnClick(R.id.noSimpleDialogButton)
    public void noClickButton(){
        dismiss();
    }
}
