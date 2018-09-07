package ritesh.com.demoadminapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ritesh.com.demoadminapp.model.User;
import ritesh.com.demoadminapp.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;


    private MainActivityViewModel mViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        isEmailVerified();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }
    }
    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }
    private void isEmailVerified() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    final FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (null != currentUser) {
                        for(UserInfo userinfo: currentUser.getProviderData()){
                            if ("password".equals(userinfo.getProviderId())) {
                                if(currentUser.isEmailVerified()){
                                    Toast.makeText(MainActivity.this, "Email verified", Toast.LENGTH_SHORT).show();
                                    updateEmailVerifiedToFirebaseDb(mAuth.getCurrentUser());
                                }else{
                                    Toast.makeText(MainActivity.this, "Email not verified", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            });

        }
//         return false;
    }

    private void startSignIn() {
        // Choose authentication providers
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Sign in with FirebaseUI
//        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
//                .setAvailableProviders(Collections.singletonList(
//                        new AuthUI.IdpConfig.EmailBuilder().build()))
//                .setIsSmartLockEnabled(false)
//                .build();
Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            } else {
                final FirebaseUser currentUser = mAuth.getCurrentUser();

                if (null != currentUser) {

                    for(UserInfo userinfo: currentUser.getProviderData()){
                        if ("password".equals(userinfo.getProviderId())) {
                            if(!currentUser.isEmailVerified()){
                                /* Send Verification Email */
                                currentUser.sendEmailVerification()
                                        .addOnCompleteListener(this, new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                /* Check Success */
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Verification Email Sent To: " + currentUser.getEmail(),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.e(TAG, "sendEmailVerification", task.getException());
                                                    Toast.makeText(getApplicationContext(),
                                                            "Failed To Send Verification Email!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                    addToFirebaseDb(currentUser);

                    String str1 = currentUser.getDisplayName();
                    String str2 = currentUser.getEmail();
                    Task<GetTokenResult> str3 = currentUser.getIdToken(true);
                    FirebaseUserMetadata str4 = currentUser.getMetadata();
                    String str5 = currentUser.getPhoneNumber();
                    Uri str6 = currentUser.getPhotoUrl();
                    String str7 = currentUser.getUid();
                    String str8 = currentUser.getDisplayName();
                }
            }
        }
    }

    public void addToFirebaseDb(FirebaseUser user){

        String providername = "";
        if (user.getProviders() == null || user.getProviders().isEmpty()) {
        } else {
            Iterator<String> providerIter = user.getProviders().iterator();
            while (providerIter.hasNext()) {
                String provider = providerIter.next();
                switch (provider) {
                    case GoogleAuthProvider.PROVIDER_ID:
                        providername = "Google";
                        break;
                    case FacebookAuthProvider.PROVIDER_ID:
                        providername = "Facebook";
                        break;
                    case TwitterAuthProvider.PROVIDER_ID:
                        providername = "Twitter";
                        break;
                    case EmailAuthProvider.PROVIDER_ID:
                        providername = "Email";
                        break;
                    case PhoneAuthProvider.PROVIDER_ID:
                        providername = "Phone";
                        break;
                    default:
                        throw new IllegalStateException("Unknown provider: " + provider);
                }

            }
        }
        //get reference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

//build child
        User userdata = new User();
//        new User(user.getUid(),
//                user.getEmail(),user.getPhoneNumber(),
//                user.getDisplayName(), providername
//                , user.getPhotoUrl().toString(), user.getPhotoUrl().toString()
//                , false, false)
        userdata.setUid(user.getUid());
        userdata.setUname(user.getDisplayName());
        if (null!=user.getEmail()) {
            userdata.setEmail_id(user.getEmail());
        }
        if (null!=user.getPhoneNumber()) {
            userdata.setMobile(user.getPhoneNumber());
        }
        if (null!=user.getPhotoUrl()) {
            userdata.setPhoto_url(user.getPhotoUrl().toString());
            userdata.setStorage_url(user.getPhotoUrl().toString());
            userdata.setmPhotoUri(user.getPhotoUrl());
        }
        userdata.setProvider(providername);
        userdata.setAdmin(false);
        userdata.setAuthor(false);

        ref.child(user.getUid()).setValue(userdata);
    }

    public void updateEmailVerifiedToFirebaseDb(FirebaseUser user){
        //get reference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(user.getUid()).child("isEmailVerified").setValue(true);
    }


}
