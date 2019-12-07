package com.example.zapper2;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewOrder extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView textView;
    String order="";
    StringBuilder sb;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ArrayList<String> veggies;
    Button delivered;
    ArrayList<String> quantity;
    String docid="";
    ArrayList<String> price;
    String pos;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        textView=findViewById(R.id.orderdetails);
        delivered=findViewById(R.id.delivered);
        sb=new StringBuilder();
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        veggies=new ArrayList<>();
        quantity=new ArrayList<>();
        price=new ArrayList<>();
        Intent intent=getIntent();
        final String docid=intent.getStringExtra("docid");
        this.docid=docid;
        pos=intent.getStringExtra("pos");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        filltextView();
        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                try {
                    Thread.sleep(3000);
                    Intent intent=new Intent(ViewOrder.this,Orders.class);
                    intent.putExtra("pos",pos);
                    startActivity(intent);
                    db.collection("users").document(docid).update("delivered","yes");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void filltextView() {
        Intent intent=getIntent();
        String docid=intent.getStringExtra("docid");
        db.collection("users")
                .document(docid)
                .collection("cart")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int c=0;
                int sum=0;
                for(QueryDocumentSnapshot querySnapshot:queryDocumentSnapshots)
                {
                    String vege=querySnapshot.getId();
                    veggies.add(vege);
                    String qno=querySnapshot.get("quantity").toString();
                    String cost=querySnapshot.get("price").toString();
                    int total=Integer.parseInt(cost)*Integer.parseInt(qno);
                    c=c+1;
                    sum=sum+total;
                    sb.append(String.valueOf(c)+". "+vege+" x ₹"+qno+" ------------ "+total+"\n");
                }
                sb.append("____________________________\n");
                sb.append("Total: ₹"+sum);
                textView.setText(sb.toString());


            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        db.collection("users").document(docid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap<String,Object> location=new HashMap<>();
                location=(HashMap<String,Object>)documentSnapshot.get("location");
                String lat=location.get("latitude").toString();
                String lon=location.get("longitude").toString();

                LatLng latLng=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Delivery Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.setMinZoomPreference(5);
            }

        });

        // Add a marker in Sydney and move the camera

    }

}
