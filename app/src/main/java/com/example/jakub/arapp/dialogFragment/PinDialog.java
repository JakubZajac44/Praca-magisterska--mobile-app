package com.example.jakub.arapp.dialogFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.jakub.arapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PinDialog extends DialogFragment {

    public static final String TAG = PinDialog.class.getSimpleName();
    public static final String PIN_KEY = "FINGERPRINT_AUTH_STATUS";
    public static final int DIALOG_PIN_FRAGMENT = 1;
    private Unbinder unbinder;

    @BindView(R.id.savedPinText)
    EditText savedPinText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME,0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pin_dialog_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        setCancelable(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.setDialogProperties();
    }

    private void setDialogProperties() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.dimAmount = 0.8f;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @OnClick(R.id.confirmSavePinButton)
    public void confirmSavePinButton(){
        String pin = savedPinText.getText().toString();
        if(pin.length()>=4){
            dismiss();
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(PIN_KEY,pin));
        }
    }

}
