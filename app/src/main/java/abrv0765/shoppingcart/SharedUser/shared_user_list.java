package abrv0765.shoppingcart.SharedUser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import abrv0765.shoppingcart.AddList.add_list_Adapter;
import abrv0765.shoppingcart.HelperClasses.ListNodeHelper;
import abrv0765.shoppingcart.HelperClasses.Paths;
import abrv0765.shoppingcart.R;

import static abrv0765.shoppingcart.FirebaseApplication.encoded_email;

public class shared_user_list extends AppCompatActivity {
    private add_list_Adapter adap;
    private ListView mListView;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        mListView=(ListView)findViewById(R.id.add_list_listView);
        user=FirebaseAuth.getInstance().getCurrentUser();
        setTitle("Shared Lists");
        FirebaseUser us= FirebaseAuth.getInstance().getCurrentUser();
        Firebase ref=new Firebase(Paths.Project_path+encoded_email(us.getEmail()+"/SharedLists"));

        //adap=new add_list_Adapter(shared_user_list.this,ListNodeHelper.class,R.layout.layout_list_home_view,ref,user.getDisplayName().toString());
        adap=new add_list_Adapter(shared_user_list.this,ListNodeHelper.class,R.layout.layout_list_home_view,ref,encoded_email(user.getEmail()));
        mListView.setAdapter(adap);
        this.adap.notifyDataSetChanged();
//add listner
        mListView.setClickable(true);/*
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(view.getContext(), ListItem.class);
                //  Toast.makeText(MainActivity.this, "gREAT dONE", Toast.LENGTH_SHORT).show();
                String listId = adap.getRef(position).getKey();
                Toast.makeText(shared_user_list.this, listId, Toast.LENGTH_SHORT).show();
                i.putExtra("list_id",listId);
                startActivity(i);
            }
        });*/


    }






}
