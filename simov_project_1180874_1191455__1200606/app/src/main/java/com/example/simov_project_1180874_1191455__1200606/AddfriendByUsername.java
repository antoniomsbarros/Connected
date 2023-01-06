package com.example.simov_project_1180874_1191455__1200606;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.simov_project_1180874_1191455__1200606.Entity.Friends;
import com.example.simov_project_1180874_1191455__1200606.Entity.FriendsStatus;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class AddfriendByUsername extends AppCompatActivity implements ConfirmationDialog.DialogListener {

    private ListView listView;
    private DatabaseReference databaseReference=null;
    private DatabaseReference databaseReferenceFriends=null;

    private FirebaseDatabase database;
    private ArrayList<String> names;
    private ArrayList<String>uuids;
    private String useruuid;

    private String secundUseruuid;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend_by_username);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference().child("users");
        databaseReferenceFriends=database.getReference().child("friends");
        getallUsersNames();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser==null){
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);
            finish();
            return;
        }
        useruuid=currentUser.getUid();
        listView=findViewById(R.id.listview);
        arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openDialog();
                secundUseruuid=uuids.get(i);
            }
        });
    }

    private void getallUsersNames() {
        names=new ArrayList<>();
        uuids=new ArrayList<>();
        ArrayList<User> allusers=new ArrayList<>();
        ArrayList<String> arrayList=new ArrayList<>();
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    if (!useruuid.equals(ds.getKey())){
                        String name=ds.child("fullname").getValue(String.class);
                        User user=ds.getValue(User.class);
                       names.add(name);
                        uuids.add(ds.getKey());
                        allusers.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ValueEventListener valueEventListener1=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Friends friends=ds.getValue(Friends.class);
                    if (friends.getUuidUserA().equals(useruuid) || friends.getUuiduserb().equals(useruuid)){
                        if (friends.getUuidUserA().equals(useruuid)){
                            arrayList.add(friends.getUuiduserb());
                        }else {
                            arrayList.add(friends.getUuidUserA());
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReferenceFriends.addListenerForSingleValueEvent(valueEventListener1);
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
        for (int i = 0; i < arrayList.size(); i++) {
            int tem=-1;
            tem=uuids.indexOf(arrayList.get(i));
            if (tem!=-1){
                uuids.remove(tem);
                names.remove(tem);
            }
        }



        User currentUser=new User();
        ArrayList<User> friends=new ArrayList<>();
        for (int i = 0; i < allusers.size(); i++) {
            if (useruuid.equals(uuids.get(i))){
                currentUser=allusers.get(i);
            }
        }


/*
        for (String friend: currentUser.getFriends()) {
            for (int i = 0; i < uuids.size(); i++) {
                if (uuids.get(i).equals(friend)){
                    friends.add(allusers.get(i));
                    arrayList.add(new Pair<>(uuids.get(i),allusers.get(i)));
                }
            }
        }

        for (int i = 0; i < friends.size(); i++) {
        }
        ArrayList<String>tempuiids=new ArrayList<>();
        ArrayList<String> tempNames=new ArrayList<>();
        allusers.removeAll(friends);
        for (int i = 0; i < allusers.size(); i++) {
            for (int j = 0; j < names.size(); j++) {
                if (names.get(j).equals(allusers.get(i).fullname)){
                    tempuiids.add(uuids.get(j));
                    tempNames.add(names.get(j));
                }
            }
        }
        uuids=tempuiids;
        names=tempNames
        ///pensar bem
*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem =menu.findItem(R.id.search_user);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search users");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

   @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        System.out.println(id);
        if (id==R.id.search_user){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialog(){
        ConfirmationDialog dialog=new ConfirmationDialog();
        dialog.show(getSupportFragmentManager(),"Example");
    }

    @Override
    public void onConfirmed() {
        if (secundUseruuid!=null){
            databaseReferenceFriends.push().setValue(new Friends(useruuid,secundUseruuid,FriendsStatus.Pending)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(AddfriendByUsername.this,"Request Sent success",Toast.LENGTH_SHORT).show();
                    showMainActivity();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddfriendByUsername.this,"Request Sent failed",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onCancelled() {
    }

    private void showMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}