package com.example.cinem.ui.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class SelectMovie extends AppCompatActivity {


    int cinema_id;
    String email;
    LinearLayout layout;
    TextView cinema_name;
    TextView morada;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_movie);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cinema_id = extras.getInt("cinema_id");
            email = extras.getString("email");
        }

        layout = findViewById(R.id.container);
        cinema_name = findViewById(R.id.cinema_name);
        morada = findViewById(R.id.morada);
        img = findViewById(R.id.imageView);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cinemas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                if (Integer.parseInt(document.getId()) == cinema_id) {
                                    cinema_name.setText((String) document.get("name"));
                                    morada.setText((String) document.get("address"));
                                    Picasso.get().load((String) document.get("img")).resize(500, 300).into(img);
                                }

                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("filmes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                addCard((String) document.get("name"), (String) document.get("img"), document.getId() );
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void addCard(String name, String img, String filme_id) {
        final View view = getLayoutInflater().inflate(R.layout.list, null);

        TextView nameView = view.findViewById(R.id.ticket_list);
        ImageView movie_img = view.findViewById(R.id.movieImg);


        nameView.setText(name);
        Picasso.get().load(img).resize(200, 250).into(movie_img);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HI","view");
                Intent intent = new Intent(SelectMovie.this, MovieInfo.class)
                        .putExtra("cinema_id",cinema_id)
                        .putExtra("filme_id",filme_id)
                        .putExtra("email",email);

                startActivity(intent);
            }
        });


        layout.addView(view);
    }
}
