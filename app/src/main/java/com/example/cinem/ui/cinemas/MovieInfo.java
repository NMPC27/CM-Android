package com.example.cinem.ui.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cinem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieInfo extends AppCompatActivity {

    String filme_id;
    String email;
    int cinema_id;

    ImageView img;
    TextView movie_title;
    TextView age;
    TextView time;
    TextView categorias;
    TextView descricao;
    Button buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_info);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            filme_id = extras.getString("filme_id");
            cinema_id = extras.getInt("cinema_id");
            email = extras.getString("email");
        }

        img = findViewById(R.id.movie_img);
        movie_title = findViewById(R.id.movieTitle);
        age = findViewById(R.id.age);
        time = findViewById(R.id.time);
        categorias = findViewById(R.id.categorias);
        descricao = findViewById(R.id.descricao);
        buy = findViewById(R.id.buyTicket);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("filmes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Log.d("TAG",document.getId());
                                if ( document.getId().equals(filme_id) ) {
                                    Log.d("GUD", document.getId() + " => " + document.getData());
                                    Picasso.get().load((String) document.get("img")).resize(600, 750).into(img);
                                    movie_title.setText((String) document.get("name"));
                                }

                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://imdb-api.com/en/API/Title/k_6anb9q23/"+filme_id;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        // Display the first 500 characters of the response string.
                        try {
                            age.setText((String) res.get("contentRating"));
                            time.setText((String) res.get("runtimeStr"));
                            categorias.setText((String) res.get("genres"));
                            descricao.setText((String) res.get("plot"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("API","API ERROR");
                }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HI","view");
                Intent intent = new Intent(MovieInfo.this, SelectSchedule.class)
                        .putExtra("cinema_id",cinema_id)
                        .putExtra("filme_id",filme_id)
                        .putExtra("email",email);

                startActivity(intent);
            }
        });


    }
}