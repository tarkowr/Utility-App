package com.rt.utility;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rt.utility.dataservice.UserDataService;
import com.rt.utility.helpers.JavaUtils;
import com.rt.utility.models.User;

public class CreateUserFragment extends Fragment {

    private EditText username;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        username = view.findViewById(R.id.editUsername);

        final UserDataService dataService = UserDataService.get(getActivity());

        // Creates a create account button onClick event to create a new user account
        view.findViewById(R.id.btnCreateAccount).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String name = username.getText().toString();
                final int sbFontSize = 20;

                if(!isValidUsername(name)){
                    JavaUtils.ShowSnackbar(view.findViewById(R.id.rootCreateFragment),
                            getString(R.string.txt_invalid_username), sbFontSize);
                    return;
                }

                User user = new User(name);
                dataService.addUser(user);

                launchHomeActivity(user);
            }
        });

        return view;
    }

    /*
    Validation method to ensure the user enters a valid username
     */
    private Boolean isValidUsername(String str){
        final String regex = "^[a-zA-Z0-9 .-]{1,40}$";

        if (JavaUtils.CheckIfEmptyString(str)){
            return false;
        }

        return str.matches(regex);
    }

    /*
    Removes this fragment from the host activity, finishes the host, launches the home , and passes it a user object
     */
    private void launchHomeActivity(User user){
        SplashActivity hostActivity = (SplashActivity)getActivity();

        if (hostActivity == null) return;

        hostActivity.removeFragment(CreateUserFragment.this);
        Intent intent = HomeActivity.newIntent(getActivity(), user.getId());

        startActivity(intent);
        hostActivity.finishActivity();
    }
}
