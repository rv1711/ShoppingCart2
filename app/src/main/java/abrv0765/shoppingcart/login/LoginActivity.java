package abrv0765.shoppingcart.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import abrv0765.shoppingcart.HelperClasses.user_data;
import abrv0765.shoppingcart.MainActivity;
import abrv0765.shoppingcart.HelperClasses.Paths;
import abrv0765.shoppingcart.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static abrv0765.shoppingcart.FirebaseApplication.encoded_email;

public  class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
private ProgressDialog mAuthProgressDialog;
    private FirebaseAuth mAuth;
   private  EditText email;
    private FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;
private TextView userNmae;
    Dialog signUp;
    Dialog forgDialog;
    EditText reg_email,reg_username;
    EditText reg_password;


    private ProgressDialog pd;

    String us;
    @Override
    protected void onStart() {
        super.onStart();
         user=FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null) {
            us=user.getEmail();
            updateUII();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.username);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,this  /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage("Authenticating ");
        mAuthProgressDialog.setCancelable(false);

        CircleImageView btnSingUp=(CircleImageView) findViewById(R.id.SignUp);
        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 signUp= new Dialog(LoginActivity.this);
                signUp.setContentView(R.layout.activity_sign_up);
                signUp.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                reg_email = (EditText) signUp.findViewById(R.id.reg_email);
                reg_password = (EditText) signUp.findViewById(R.id.reg_pwd);
                reg_username=(EditText)signUp.findViewById(R.id.reg_user);
                final EditText reg_cpwd=(EditText)signUp.findViewById(R.id.reg_pwdc);
                pd = new ProgressDialog(signUp.getContext());
                us=reg_username.getText().toString();
                pd.setTitle(getString(R.string.progress_dialog_loading));
                pd.setMessage("Authenticating");
                pd.setCancelable(false);
                mAuth = FirebaseAuth.getInstance();
                Button btnSingUp=(Button)signUp.findViewById(R.id.registerbutton);
                btnSingUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(reg_cpwd.getText().toString().equals(reg_password.getText().toString())){
                            passSign();
                        }
                        else {
                            reg_cpwd.setText(null);
                            reg_cpwd.setError("Password not matching");
                            reg_cpwd.requestFocus();
                        }

                    }
                });


                signUp.show();
                /*Intent i;
                i=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);*/
           /*     SignUpActivity sgn = new SignUpActivity();

                sgn.show(getFragmentManager(),"Sign Up");
                sgn.dismiss();*/

            }
        });
        TextView forgpwd=(TextView)findViewById(R.id.forgfpwd);
        forgpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgDialog= new Dialog(LoginActivity.this);
                forgDialog.setContentView(R.layout.dialog_share_list);
                forgDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                forgDialog.show();
                Button forgButton=(Button)forgDialog.findViewById(R.id.share_list_btn);
                forgButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // pass_forget(v);

                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        EditText email_forg=(EditText)forgDialog.findViewById(R.id.share_list);
                        if(!email_forg.getText().toString().isEmpty()) {
                            auth.sendPasswordResetEmail(email_forg.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                                Toast.makeText(LoginActivity.this, "Password Link sent", Toast.LENGTH_SHORT).show();
                                                forgDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            email_forg.setError("Not Valid");
                            email_forg.requestFocus();
                        }
                    }
                });
            }
        });



setupGoogleSignIn();
    }
    private void setupGoogleSignIn() {
        SignInButton signInButton = (SignInButton)findViewById(R.id.google_signin);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
mAuthProgressDialog.show();        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user_data's information
                            Log.d(TAG, "signInWithCredential:success");
                             user = mAuth.getCurrentUser();
                            user_data ud=new user_data( user.getEmail(),user.getDisplayName());
                            Firebase ref=new Firebase(Paths.Project_path+Paths.User_node);
                            ref.child(encoded_email(user.getEmail())).setValue(ud);

                            updateUII();
                            mGoogleApiClient.clearDefaultAccountAndReconnect();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user_data.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI();
                        }

                        // [START_EXCLUDE]
mAuthProgressDialog.hide();                        // [END_EXCLUDE]
                    }
                });
    }



    public void signInGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);





    }

    public void pasword_signin(View view){

        EditText password=(EditText)findViewById(R.id.password);

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user_data's information
                            Log.d(TAG, "signInWithEmail:success");
                             user = mAuth.getCurrentUser();
                                us=user.getEmail();
                                                updateUII();
                                                finish();


                        } else {
                            // If sign in fails, display a message to the user_data.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI();
                        }

                        // ...
                    }
                });



    }

    public void   passSign(){
        final String mail=reg_email.getText().toString();
        String pass=reg_password.getText().toString();
        us=reg_username.getText().toString();
        // fireApp.setUser(userdata);
        if(isEmailValid(mail)==true&&isPasswordValid(pass)==true&&isUserNameValid(us)==true) {
            pd.show();
            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user_data's information
                                Log.d(TAG, "createUserWithEmail:success");
                                user = mAuth.getCurrentUser();
                                // update();
                                user_data ud=new user_data( user.getEmail(),us);
                                //fireApp.setUser(ud);
                                //   Log.v("hello2",fireApp.getUser().getEmail()+" "+fireApp.getUser().getName());
                                Log.v("hello", user.getEmail()+" "+us);

                                Firebase ref=new Firebase(Paths.Project_path+Paths.User_node);
                                ref.child(encoded_email(user.getEmail())).setValue(ud);

                                Toast.makeText(LoginActivity.this, "Succesful", Toast.LENGTH_SHORT).show();
                                pd.hide();

                                updateUII();
                                signUp.dismiss();
                                //finish();

                            } else {
                                // If sign in fails, display a message to the user_data.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                                pd.hide();
                            }


                        }
                    });
        }


    }

public void updateUII(){

   // Log.v("activity123",user.getDisplayName());
    Intent i=new Intent(LoginActivity.this,MainActivity.class);
    i.putExtra("name",us);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

  //  i.putExtra("UserCurrent",user_data.getDisplayName()) ;
    startActivity(i);

}
public void pass_forget(View v){

    FirebaseAuth auth = FirebaseAuth.getInstance();
    EditText email_forg=(EditText)v.findViewById(R.id.shared_list);
    auth.sendPasswordResetEmail(email_forg.getText().toString())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                        Toast.makeText(LoginActivity.this, "Password Link sent", Toast.LENGTH_SHORT).show();
                        forgDialog.dismiss();
                    }
                }
            });




}



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void updateUI(){

        signUp.dismiss();

    }




    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            this.reg_email.setError("Invalid E_mail");
            return false;
        }
        return isGoodEmail;
    }

    private boolean isUserNameValid(String userName) {
        if (userName.isEmpty()) {
            reg_username.setError("Give correct Name");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            this. reg_password.setError("Invalid Password");
            return false;
        }
        return true;
    }


    public void update(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });



    }
}
