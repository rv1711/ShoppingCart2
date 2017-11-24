package abrv0765.shoppingcart.AddList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import abrv0765.shoppingcart.HelperClasses.ListNodeHelper;
import abrv0765.shoppingcart.HelperClasses.Paths;
import abrv0765.shoppingcart.ListDetail.ListItem;
import abrv0765.shoppingcart.MainActivity;
import abrv0765.shoppingcart.R;

import static abrv0765.shoppingcart.FirebaseApplication.encoded_email;
import static android.system.Os.remove;

/**
 * Created by Ayush on 21-Oct-17.
 */

public class add_list_Adapter extends FirebaseListAdapter<ListNodeHelper> {
    LayoutInflater inflater;
    View view;
    EditText listName;
    String useremail;

    String listId;
    boolean share;
    Activity temp;
    public add_list_Adapter(Activity activity, Class<ListNodeHelper> modelClass, int modelLayout, Query ref,String useremail ) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity=activity;
        this.useremail=useremail;
        temp=activity;

        if(temp.toString().contains("MainActivity")) {
            share=false;
        }
        else {
           share=true;
        }
    }


    @Override
    protected void populateView(View v, final ListNodeHelper model, final int position) {
        view =v;
        this.notifyDataSetChanged();

        TextView liat_name=(TextView)v.findViewById(R.id.card_ListName);
        TextView owner_name=(TextView)v.findViewById(R.id.owner);
        TextView total_user=(TextView)v.findViewById(R.id.card_ListNo);
        ImageView del = (ImageView)v.findViewById(R.id.delete);
        if(model==null)
        {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(),android.R.anim.slide_in_left);
            view.startAnimation(animation);
        }

        else if(model!=null){
            Log.v("abcdm",temp.toString());
            liat_name.setText(model.getListName());
            owner_name.setText(model.getUserName());
            final String item_id=this.getRef(position).getKey();
            total_user.setText(" "+model.getTotalUsers());
            v.setClickable(true);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(v.getContext(),ListItem.class);

                    listId = getRef(position).getKey();
                    Toast.makeText(v.getContext(), "gREAT dONE "+listId+" "+model.getListName(),Toast.LENGTH_SHORT).show();
                    i.putExtra("list_id",listId);
                    i.putExtra("activity_name",temp.toString());
                    v.getContext().startActivity(i);
                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteList(item_id);
                }
            });
        }



        //listName=(EditText) v.findViewById(R.id.add_listname);
//
// listName.setHint("Edit List Name");

        final String item_id=this.getRef(position).getKey();
        ImageView iv=(ImageView)v.findViewById(R.id.card_ListEdit);
        iv.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                inflater=mActivity.getLayoutInflater();
                v=inflater.inflate(R.layout.fragment_add_list,null);
                listName=(EditText) v.findViewById(R.id.add_listname);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity).setView(v)
                        .setTitle(mActivity.getString(R.string.editListname))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                updateList(item_id) ;
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /* Dismiss the dialog */
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });




    }


    public void    updateList(String item_id) {

        Firebase firebaseRef;
        if(temp.toString().contains("MainActivity")) {
            firebaseRef = new Firebase(Paths.Project_path + "/" + useremail + "/"
                    + "Lists" + "/" + item_id);
        }
        else {
            firebaseRef = new Firebase(Paths.Project_path + "/" + useremail + "/"
                    + "SharedLists" + "/" + item_id);
        }
        Log.v("hello",firebaseRef.toString());
        Log.v("hello1",listName.getText().toString());

        /* Make a map for the removal */
        HashMap<String, Object> updatedRemoveItemMap = new HashMap<String, Object>();

        /* Remove the item by passing null */
        updatedRemoveItemMap.put("listName",listName.getText().toString());
        Log.v("hello1",listName.getText().toString());



        /* Do the update */
        firebaseRef.updateChildren(updatedRemoveItemMap);




    }
    public void deleteList(String item_id){

         final Firebase ref,uref;
         if(!share){
        uref = new Firebase(Paths.Project_path + useremail + "/Lists/" + item_id);}
        else {
        uref=new Firebase(Paths.Project_path + useremail+"/SharedLists/"+item_id);}
        ref=new Firebase(Paths.Project_path+"listItems/"+item_id);
         final Firebase reff = new Firebase(Paths.Project_path+"lists/"+item_id);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext()).
                setTitle(view.getContext().getString(R.string.remove_item_list))
                .setMessage(view.getContext().getString(R.string.dialog_message_are_you_sure_remove_item))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        uref.removeValue();
                        if(!share){
                            ref.removeValue();
                        reff.removeValue();}
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                                /* Dismiss the dialog */
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

}
