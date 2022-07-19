package com.example.socialmediaintegration;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class SecondActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView name,email;
    ImageView picture;
    String origin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getSupportActionBar().hide();
        name = findViewById(R.id.txtviewname);
        email = findViewById(R.id.txtviewemail);
        picture = findViewById(R.id.imgdp);

        Intent intent = getIntent();
        origin = intent.getStringExtra("origin");
        if(origin.equals("google")){
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this,gso);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account != null){
                String userName = account.getDisplayName();
                String userEmail = account.getEmail();
                String url = account.getPhotoUrl().toString();
                Picasso.get().load(url).into(picture);
                name.setText(userName);
                email.setText(userEmail);
            }
        }
        else if(origin.equals("facebook")){
            //for facebook
            AccessToken accessToken= AccessToken.getCurrentAccessToken();
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            try {
                                String fullName = object.getString("name");
                                name.setText(fullName);
                                String url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                Picasso.get().load(url).into(picture);
                                String userEmail = object.getString("email");
                                email.setText(userEmail);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,link,picture.type(large)");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    public void googleSignOut(View view) {
        if(origin.equals("google")){
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(SecondActivity.this,MainActivity.class));
                }
            });
        }
        else if(origin.equals("facebook")){
            LoginManager.getInstance().logOut();
            startActivity(new Intent(SecondActivity.this,MainActivity.class));
            finish();
        }
    }
}