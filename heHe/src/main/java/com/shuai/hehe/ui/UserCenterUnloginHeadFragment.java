package com.shuai.hehe.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shuai.hehe.R;
import com.shuai.hehe.ui.base.BaseFragment;


public class UserCenterUnloginHeadFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_center_head_unlogin, container, false);
		
		
		return view;
	}
	
}
