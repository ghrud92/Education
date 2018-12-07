package com.hogle.education.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hogle.education.Listeners.IfDialogListener;
import com.hogle.education.Model.Code;
import com.hogle.education.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IfDialog extends Dialog {
    @BindView(R.id.condition_spinner) Spinner conditionSpinner;
    @BindView(R.id.true_spinner) Spinner trueSpinner;
    @BindView(R.id.false_spinner) Spinner falseSpinner;

    private IfDialogListener listener;

    public void setListener(IfDialogListener listener) {
        this.listener = listener;
    }

    public IfDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_if);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.complete_btn)
    void completeButton (View view) {
        Code code = new Code(Code.IF);
        code.setIfCondition(conditionSpinner.getSelectedItemPosition() + 100);
        code.getTrueCode().setCode(trueSpinner.getSelectedItemPosition() + 1);
        code.getFalseCode().setCode(falseSpinner.getSelectedItemPosition() + 1);
        listener.sendCode(code);
        dismiss();
    }

    @OnClick(R.id.cancel_btn)
    void cancelButton (View view) {
        dismiss();
    }
}
