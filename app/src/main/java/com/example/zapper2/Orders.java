package com.example.zapper2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Orders extends AppCompatActivity {
     ListView listView;
    final ArrayList<String> orders=new ArrayList<>();
    final ArrayList<String> status=new ArrayList<>();
    final ArrayList<String> items=new ArrayList<>();
    final ArrayList<Integer> images=new ArrayList<>();
    final ArrayList<String> datetimes=new ArrayList<>();
    final ArrayList<String> amounts=new ArrayList<>();
    ArrayList<String> ids=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        listView=findViewById(R.id.listView);
        Context context;
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                {
                   String orderno= queryDocumentSnapshot.get("orderno").toString();
                   String item=queryDocumentSnapshot.get("items").toString();
                   String datetime=queryDocumentSnapshot.get("datetime").toString();
                   String amount=queryDocumentSnapshot.get("amount").toString();
                   String docid=queryDocumentSnapshot.getId();
                   ids.add(docid);
                   orders.add(orderno);
                   items.add(item);
                   datetimes.add(datetime);
                   amounts.add(amount);
                   String delivered=queryDocumentSnapshot.get("delivered").toString();
                   if(delivered.equals("yes"))
                   {
                       images.add(R.drawable.ic_check_black_24dp);
                       status.add("Yes");
                   }
                   else
                       images.add(R.drawable.ic_access_time_black_24dp);
                       status.add("No");
                   break;

                }
                setupadapter();

            }
        });
        CircleImageView dp=findViewById(R.id.dp);
        dp.setImageResource(R.drawable.ic_person_black_24dp);
        TextView zappername=findViewById(R.id.zappername);
        zappername.setText("Zapper Name: Buyya");

    }

    private void setupadapter() {
        StatusAdapter adapter=new StatusAdapter(Orders.this,orders,items,images,datetimes,amounts,status);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //  Intent intent=new Intent(Orders.this,ViewOrder.class);
              //  intent.putExtra("docid",ids.get(position));
               // startActivity(intent);
            }
        });
    }
}
