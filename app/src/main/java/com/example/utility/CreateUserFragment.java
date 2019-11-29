package com.example.utility;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.utility.dataservice.UserDataService;
import com.example.utility.helpers.JavaUtils;
import com.example.utility.models.User;

public class CreateUserFragment extends Fragment {

    private EditText username;
    private TextView accountName;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        username = view.findViewById(R.id.editUsername);
        accountName = view.findViewById(R.id.txtAccountName);

        final UserDataService dataService = UserDataService.get(getActivity());
        final String accountText = getResources().getString(R.string.txt_account_name);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                accountName.setText(accountText + " " + charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        view.findViewById(R.id.btnCreateAccount).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = username.getText().toString();

                if(!isValidUsername(name)){
                    JavaUtils.ShowToast(getActivity(), R.string.txt_invalid_username);
                    return;
                }

                User user = new User(name);
                dataService.addUser(user);

                launchHomeActivity(user);
            }
        });

        view.findViewById(R.id.txtExit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SplashActivity hostActivity = (SplashActivity)getActivity();
                hostActivity.removeFragment(CreateUserFragment.this);
                hostActivity.finishActivity();
            }
        });

        return view;
    }

    private Boolean isValidUsername(String str){
        String regex = ".*[a-zA-Z0-9].*";
        final int MAX_LENGTH = 40;

        if(str.equals(null) || str.isEmpty()){
            return false;
        }

        if(!str.matches(regex)){
            return false;
        }

        if(str.length() > MAX_LENGTH){
            return false;
        }

        return true;
    }

    private void launchHomeActivity(User user){
        SplashActivity hostActivity = (SplashActivity)getActivity();
        hostActivity.removeFragment(CreateUserFragment.this);
        Intent intent = HomeActivity.newIntent(getActivity(), user.getId());
        startActivity(intent);
        hostActivity.finishActivity();
    }
}
