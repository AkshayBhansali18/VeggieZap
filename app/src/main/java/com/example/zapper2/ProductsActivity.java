package com.example.zapper2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class ProductsActivity extends AppCompatActivity {

    static ProductsActivity instance;
    private String TAG = "ProductsActivity";
    FirebaseFirestore db;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    LinkedHashSet<String> setItems = new LinkedHashSet<>();
    ArrayList<String> newItems = new ArrayList<>();
    ArrayList<String> newPrices = new ArrayList<>();
    RecyclerViewAdapter1 adapter;
    ConstraintLayout layout;

    public static ProductsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        db = FirebaseFirestore.getInstance();
        layout = findViewById(R.id.productActivity);
        instance = this;

        FloatingActionButton add = findViewById(R.id.addProduct);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProducts();
            }
        });
    }

    private void addProducts() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_window1,null);

        final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        popupWindow.setFocusable(true);
        popupWindow.update();

        final EditText EditTextAddProduct = customView.findViewById(R.id.EditTextAddProduct);

        final EditText EditTextAddPrice = customView.findViewById(R.id.EditTextAddPrice);

        Button AddYes = customView.findViewById(R.id.AddYes);
        AddYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = EditTextAddProduct.getText().toString();
                String price = EditTextAddPrice.getText().toString();
                if(name.equalsIgnoreCase("") || price.equalsIgnoreCase("")){
                    Toast.makeText(ProductsActivity.this, "The fields are empty", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }
                boolean flag = setItems.add(name);
                if(flag){
                    popupWindow.dismiss();
                    newItems.add(name);
                    newPrices.add(price);
                    uploadNewItems();
                }
                else{
                    Toast.makeText(ProductsActivity.this, "The item already exists", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }
            }
        });

        Button AddCancel = customView.findViewById(R.id.AddCancel);
        AddCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void uploadNewItems() {
        for (int i = 0; i < newItems.size(); i++) {
            String name = newItems.get(i);
            String newPrice = newPrices.get(i);
            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("id", name);
            data.put("price", newPrice);
            db.collection("zapper")
                    .document("test@test.com")
                    .collection("products")
                    .document(name)
                    .set(data);
        }
        onStart();
    }

    @Override
    public void onStart(){
        super.onStart();
        try{
            items.removeAll(items);
            price.removeAll(price);
            ids.removeAll(ids);
            setItems.removeAll(setItems);
        }
        catch (Exception e) {
            Log.d(TAG, "onStart: Well GG");
        }
        UpdateUI();
    }

    private void UpdateUI() {
        db.collection("zapper")
                .document("test@test.com")
                .collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            items.add(document.get("name").toString());
                            setItems.add(document.get("name").toString());
                            price.add(document.get("price").toString());
                            ids.add(document.get("id").toString());
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: Started recycler view");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter1(this, items, price, ids, layout);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
