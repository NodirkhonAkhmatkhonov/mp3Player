package com.mobile.readyplayer.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId(savedInstanceState));
        initView(savedInstanceState);
    }

    abstract protected void initView(Bundle savedInstanceState);
    abstract protected int getLayoutId(Bundle savedInstanceState);
}
