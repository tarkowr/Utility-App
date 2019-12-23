package com.rt.utility;

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

import com.rt.utility.dataservice.UserDataService;
import com.rt.utility.helpers.JavaUtils;
import com.rt.utility.models.User;

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

        // Updates the account name txtView with the user's account name as the user types each character
        // Learned how to create a new TextWatcher class from Android Programming by The Big Nerd Ranch
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

        // Creates a create account button onClick event to create a new user account
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

        // Creates a exit button onClick event to destroy the current activity, which will exit the app
        view.findViewById(R.id.txtExit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SplashActivity hostActivity = (SplashActivity)getActivity();
                hostActivity.removeFragment(CreateUserFragment.this);
                hostActivity.finishActivity();
            }
        });

        return view;
    }

    /*
    Validation method to ensure the user enters a valid username
     */
    private Boolean isValidUsername(String str){
        String regex = ".*[a-zA-Z0-9].*";
        final int MAX_LENGTH = 40;

        if(JavaUtils.CheckIfEmptyString(str)){
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

    /*
    Removes this fragment from the host activity, finishes the host, launches the home , and passes it a user object
     */
    private void launchHomeActivity(User user){
        SplashActivity hostActivity = (SplashActivity)getActivity();
        hostActivity.removeFragment(CreateUserFragment.this);
        Intent intent = HomeActivity.newIntent(getActivity(), user.getId());
        startActivity(intent);
        hostActivity.finishActivity();
    }
}
