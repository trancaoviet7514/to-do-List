package com.example.trancaoviet.myhelper;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin, btnSignIn, btnExit;
    TextInputEditText edtUsername, edtPassword;
    DataSnapshot mDataSnapshot_User;

    Dialog dialogNoti;
    TextView Noti, btnDismissDialog;

    //login with google
    GoogleSignInClient mGoogleSignInClient;

    //login with facebook
    CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mapControl();
        addEventForControl();

        loadAllUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String Username = object.getString("email");
                                    Provider.UserName = Username.substring(0,Username.indexOf('.'));
                                    Provider.Password = "";
                                    Provider.mDataBase.child("User").child( Provider.UserName ).child("Password").setValue( true );
                                    Provider.mDataBase.child("User").child( Provider.UserName ).child("MaxID").setValue( 0 );

                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    private void loadAllUser() {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataSnapshot_User = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        Provider.mDataBase.child("User").addListenerForSingleValueEvent(valueEventListener);

    }

    private void mapControl() {

        btnLogin = findViewById(R.id.btnLogin);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnExit = findViewById(R.id.btnExit);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

        dialogNoti = new Dialog(this);
        dialogNoti.setContentView(R.layout.login_noti_dialog);
        btnDismissDialog = dialogNoti.findViewById(R.id.btnDismissDialog);
        Noti = dialogNoti.findViewById(R.id.txtMessage);

    }

    private boolean userNameInvalidate(String UserName, String Password, DataSnapshot dataSnapshot){

        if( UserName.equals("") || Password.equals("") || dataSnapshot == null)  return false;

        for( DataSnapshot child: dataSnapshot.getChildren() ) {
            if( UserName.equals( child.getKey() ) ) {
                return false;
            }
        }
        return true;
    }

    private boolean checkLogin(String UserName, String Password, DataSnapshot dataSnapshot){

        if( UserName.equals("") || Password.equals("") || dataSnapshot == null)  return false;

        for(DataSnapshot child: dataSnapshot.getChildren()){

            String server_userName = child.getKey();
            String server_password = child.child("Password").getValue().toString();

            if( UserName.equals ( server_userName ) ) {
                if (Password.equals ( server_password ) )
                    return true;
                else return false;
            }
        }
        return false;
    }

    private boolean checkEmailisReallyhasData(String Username, DataSnapshot dataSnapshot){

        if( Username.equals("") || dataSnapshot == null)  return false;

        for( DataSnapshot child: dataSnapshot.getChildren() ) {
            if( Username.equals( child.getKey() ) ) {
                return true;
            }
        }
        return false;
    }

    private void addEventForControl() {

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(1);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String Username = edtUsername.getText().toString();
                String Password = edtPassword.getText().toString();

                if( userNameInvalidate( Username, Password, mDataSnapshot_User) ) {

                    Provider.mDataBase.child("User").child( Username ).child("Password").setValue( Password );
                    Provider.mDataBase.child("User").child( Username ).child("MaxID").setValue( 0 );
                    Noti.setText("Đăng kí thành công");
                    dialogNoti.show();
                    Provider.UserName = edtUsername.getText().toString();
                    Provider.Password = edtPassword.getText().toString();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class ) );

                }
                else {
                    Noti.setText("Tên tài khoản đã tồn tại");
                    dialogNoti.show();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( checkLogin( edtUsername.getText().toString(), edtPassword.getText().toString(), mDataSnapshot_User )  ) {

                    Provider.UserName = edtUsername.getText().toString();
                    Provider.Password = edtPassword.getText().toString();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));

                } else {
                    Noti.setText("Tên đăng nhập hoặc mật khẩu không đúng");
                    dialogNoti.show();
                }
            }
        });

        btnDismissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNoti.dismiss();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String email = account.getEmail().toString();
            Uri url = account.getPhotoUrl();
            String displayName = account.getDisplayName();
            //account.
            Provider.UserName = email.substring(0,email.indexOf('.'));

            if(!checkEmailisReallyhasData(Provider.UserName,mDataSnapshot_User)){
                Provider.mDataBase.child("User").child( Provider.UserName ).child("Password").setValue( true );
                Provider.mDataBase.child("User").child( Provider.UserName ).child("MaxID").setValue( 0 );
            }

            startActivity(new Intent(LoginActivity.this,MainActivity.class));

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.


        }
    }
}

