package com.warkahot.backstagesupportersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SignUp_page extends AppCompatActivity {

    TextView user_name,pass,cnf_pass,email,phone_num;
    Button login_button;
    LinearLayout main_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);




       initialize_ui_variables();
        on_click_events();
    }

    public void initialize_ui_variables()
    {
        user_name = (TextView)findViewById(R.id.user_name);
        pass = (TextView)findViewById(R.id.login_pass);
        cnf_pass = (TextView)findViewById(R.id.login_confirm_pass);
        email = (TextView)findViewById(R.id.email);
        phone_num = (TextView)findViewById(R.id.p_number);

        main_view = (LinearLayout)findViewById(R.id.main_view);

        login_button = (Button)findViewById(R.id.login_but);
    }

    public void on_click_events()
    {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_if_all_details_filled_and_passwords_match() == true)
                {
                    Intent i = new Intent(SignUp_page.this,Map_Page.class);
                    startActivity(i);
                }
            }
        });
    }

    public boolean check_if_all_details_filled_and_passwords_match()
    {
        if(!user_name.getText().toString().equals("") && !pass.getText().toString().equals("") &&!cnf_pass.getText().toString().equals("") &&!email.getText().toString().equals("") &&!phone_num.getText().toString().equals("") )
        {
            if(pass.getText().toString().equals(cnf_pass.getText().toString()))
                return true;
            else {
                Snackbar.make(main_view, "Password not matching!!!", Snackbar.LENGTH_LONG)
                        .setAction("CLOSE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                        .show();
                return false;
            }
        }
        else
        {
            Snackbar.make(main_view, "Please fill all the Details !!!", Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
            return false;
        }
    }
}
