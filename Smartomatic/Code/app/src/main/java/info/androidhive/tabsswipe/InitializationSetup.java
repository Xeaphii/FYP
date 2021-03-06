package info.androidhive.tabsswipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InitializationSetup extends Activity implements OnClickListener {

    Button btnSignIn;
    Button btnSignUp;
    SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.initialization_setup);
        String InitStatus = prefs.getString("is_initialized", "0");
        if (InitStatus.equals("1"))
        {
            Intent GoToMainActivity = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(GoToMainActivity);
        }

        btnSignIn = (Button) findViewById(R.id.btnSingIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent i = null;
        switch(v.getId()){
            case R.id.btnSingIn:
                i = new Intent(this,SignInActivity.class);
                break;
            case R.id.btnSignUp:
                i = new Intent(this,SignUpActivity.class);
                break;
        }
        startActivity(i);
    }



}
