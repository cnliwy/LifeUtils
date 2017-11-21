package com.liwy.common.update;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.liwy.common.R;


/**
 * Created by Teprinciple on 2016/10/13.
 */
public class UpdateConfirmDialog extends Dialog {

    Callback callback;
    private TextView content;
    private TextView sureBtn;
    private TextView cancleBtn;

    public UpdateConfirmDialog(Context context, Callback callback) {
        super(context, R.style.UpdateCustomDialog);
        this.callback = callback;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_confirm, null);
        sureBtn = (TextView)mView.findViewById(R.id.dialog_confirm_sure);
        cancleBtn = (TextView)mView.findViewById(R.id.dialog_confirm_cancle);
        content = (TextView) mView.findViewById(R.id.dialog_confirm_title);


        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callback(1);
                UpdateConfirmDialog.this.cancel();
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callback(0);
                UpdateConfirmDialog.this.cancel();
            }
        });
        super.setContentView(mView);
    }


    public UpdateConfirmDialog setContent(String s){
        content.setText(s);
        return this;
    }

    /**
     * @param negative 取消按钮文字
     * @param positive 确认按钮文字
     * @return
     */
    public UpdateConfirmDialog setButtonTitle(String negative, String positive){
        cancleBtn.setText(negative);
        sureBtn.setText(positive);
        return this;
    }


}
