
package abrv0765.shoppingcart.SharedUser;

        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.app.DialogFragment;
        import android.content.DialogInterface;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.EditText;
        import android.widget.Toast;
        import static abrv0765.shoppingcart.FirebaseApplication.encoded_email;
        import static android.R.attr.id;

        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.ValueEventListener;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.gson.Gson;

        import java.util.HashMap;

        import abrv0765.shoppingcart.HelperClasses.ListNodeHelper;
        import abrv0765.shoppingcart.HelperClasses.Paths;
        import abrv0765.shoppingcart.R;


/**
 * Created by Ayush on 26-Oct-17.
 */

public class shared_user_dialog extends DialogFragment {
    LayoutInflater inflater;
    View v;
    boolean exist=false;
    EditText useremail;
    private     Firebase ref;
    private String list_id;
    ListNodeHelper shoppingList,updatedList;
    private  FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater=getActivity().getLayoutInflater();
        v=inflater.inflate(R.layout.shared_user_dialog,null);
        useremail=(EditText) v.findViewById(R.id.shared_user_email);


        Bundle bun=getArguments();
        Gson g=new Gson();

        final String list=bun.getString("list_object");
        shoppingList=g.fromJson(list,ListNodeHelper.class);
        list_id=bun.getString("list_id");
        updatedList=shoppingList;


        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(v);

        builder.setPositiveButton("Add Shared User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateUser();
                Log.v("check",useremail.getText().toString()+" "+useremail.getText().toString()+" "+user.getEmail());

//if(checkEmailExistence(useremail.getText().toString())==true&&isEmailValid(useremail.getText().toString())==true&& useremail.getText().toString()!=user.getEmail()) {

                Log.v("check",checkEmailExistence(useremail.getText().toString())+" "+isEmailValid(useremail.getText().toString())+" "+user.getEmail());


                Firebase sList = new Firebase(Paths.Project_path + encoded_email(useremail.getText().toString()) + Paths.shared_list + list_id);
                sList.setValue(updatedList);
                //


                Log.v("sharLit", sList.toString());
                Toast.makeText(getActivity(), "List Shared Succesfully", Toast.LENGTH_SHORT).show();
//}

//else{
//
//    Toast.makeText(getActivity(), "Check Email!", Toast.LENGTH_SHORT).show();
//    Toast.makeText(getActivity(), "It don't exist or wrong format ", Toast.LENGTH_SHORT).show();
//    Log.v("check",checkEmailExistence(useremail.getText().toString())+" "+isEmailValid(useremail.getText().toString())+" "+user.getEmail());
//
//
//
//}
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });



        builder.setTitle("Add Shared  User");

        AlertDialog dialog=builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }





    private boolean checkEmailExistence(String mail) {
        ref=new Firebase(Paths.Project_path+"Users/"+encoded_email(mail));
        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* If there is no user, make one */
                if( dataSnapshot.exists()){
                    exist =true;
                    Log.v("check1",dataSnapshot.getValue().toString());


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //   Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
            }
        });
        Log.v("check1",  String.valueOf(exist));


        return  exist;
    }


    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            useremail.setError("Invalid E_mail");
            return false;
        }
        return isGoodEmail;
    }




    public void updateUser(){

        Firebase ref=new Firebase(Paths.Project_path+encoded_email(user.getEmail())+"/Lists/"+list_id);

        Log.v("path",ref.toString());
        HashMap<String, Object> updatedRemoveItemMap = new HashMap<String, Object>();

        /* Remove the item by passing null */
        updatedRemoveItemMap.put("totalUsers", shoppingList.getTotalUsers()+1);



        /* Do the update */
        ref.updateChildren(updatedRemoveItemMap);

        updatedList.setTotalUsers(shoppingList.getTotalUsers()+1);



    }






}

