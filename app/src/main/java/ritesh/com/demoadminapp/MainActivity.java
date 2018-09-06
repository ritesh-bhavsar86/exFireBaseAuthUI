package ritesh.com.demoadminapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
//        if(isEmailVerified()){
//            Toast.makeText(this, "Email verified", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show();
//        }
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
//                    isEmailVerified = user.isEmailVerified();
//                    Log.d(TAG,"OnCreate in Reload()- is Email Verified: " + isEmailVerified);
//                    showToast("Is Email Verified: " + isEmailVerified);
                    final FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (null != currentUser) {
                        for(UserInfo userinfo: currentUser.getProviderData()){
                            if ("password".equals(userinfo.getProviderId())) {
                                if(currentUser.isEmailVerified()){
                                    Toast.makeText(MainActivity.this, "Email verified", Toast.LENGTH_SHORT).show();
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
                    if ("password".equals(currentUser.getProviderData().get(1).getProviderId())) {
                        if (!currentUser.isEmailVerified()) {
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

                            /* Handle Case When Email Not Verified */
                        }
                    }
                }
            }
        }
    }


}
