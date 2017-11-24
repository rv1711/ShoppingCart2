package abrv0765.shoppingcart.ListDetail;
import android.app.AlertDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.firebase.ui.FirebaseListAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import abrv0765.shoppingcart.HelperClasses.ListItemNodeHelper;
import abrv0765.shoppingcart.HelperClasses.Paths;
import abrv0765.shoppingcart.R;

import static android.system.Os.remove;

/**
 * Created by Ayush on 24-Oct-17.
 */

public class add_list_item_adapter extends FirebaseListAdapter<ListItemNodeHelper> {
    private String mListId;
    private  Firebase ref;
    private View view;
    boolean isShare;
    ArrayList<ListItemNodeHelper> al=new ArrayList<>();
    public add_list_item_adapter(Activity activity, Class<ListItemNodeHelper> modelClass, int modelLayout, Query ref, String listId,boolean share) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mListId = listId;
        this.isShare=share;
    }



    @Override
    protected void populateView(View v, ListItemNodeHelper model,int position) {

        view = v;
        TextView tv=(TextView)v.findViewById(R.id.card_itemname);
        TextView qty = (TextView)v.findViewById(R.id.required_qty);
        TextView pqty = (TextView)v.findViewById(R.id.purchased_qty);
        TextView pric = (TextView)v.findViewById(R.id.price_item);
        ImageView iv=(ImageView)v.findViewById(R.id.delete);
        ImageView done=(ImageView)v.findViewById(R.id.done);

        if(model==null)
        {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(),android.R.anim.slide_in_left);
            view.startAnimation(animation);
        }
        else if(model!=null){

            tv.setText(model.getListItemName());
            qty.setText(model.getQuantity());
            pric.setText(model.getPrice());
            pqty.setText(model.getQuantity());

            if(model.isBought()==false){

                if(!isShare) {
                    iv.setVisibility(View.VISIBLE);
                }
                else {
                    iv.setVisibility(View.GONE);
                }
                    done.setVisibility(View.GONE);
                    v.setClickable(true);
//pasted
                    final String item_id = this.getRef(position).getKey();
                    ref = new Firebase(Paths.Project_path + "listItems/" + mListId + "/" + item_id);


                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {


                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity)
                                    .setTitle(mActivity.getString(R.string.remove_item_option))
                                    .setMessage(mActivity.getString(R.string.dialog_message_are_you_sure_remove_item))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        Animation animation = AnimationUtils.loadAnimation(view.getContext(),android.R.anim.slide_out_right);
                                        view.startAnimation(animation);

                                            remove(item_id);                    /* Dismiss the dialog */
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
                    });
                    //till here
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            update_list_item_dialog ud = new update_list_item_dialog();
                            Bundle b = new Bundle();
                            b.putString("listId", mListId);
                            b.putString("Itemid", item_id);

                            ud.setArguments(b);
                            ud.show(mActivity.getFragmentManager(), "AddUser");
                        }
                    });


                add_list_item_adapter.this.notifyDataSetChanged();
            }
            else if(model.isBought()){
                iv.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                v.setClickable(false);
                tv.setClickable(false);
                add_list_item_adapter.this.notifyDataSetChanged();
            }


        }

        //from here

        this.notifyDataSetChanged();

    }


    private void remove(String itemId) {
        Firebase firebaseRef = new Firebase(Paths.Project_path);

        /* Make a map for the removal */
        HashMap<String, Object> updatedRemoveItemMap = new HashMap<String, Object>();

        /* Remove the item by passing null */
        updatedRemoveItemMap.put("/" + "listItems"+ "/"
                + mListId + "/" + itemId, null);



        /* Do the update */
        firebaseRef.updateChildren(updatedRemoveItemMap);
        add_list_item_adapter.this.notifyDataSetChanged();
    }











}
