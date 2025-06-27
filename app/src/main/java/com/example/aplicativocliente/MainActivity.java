package com.example.aplicativocliente;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MainActivity extends AppCompatActivity {

    private MapView map;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ⚠️ Configure User-Agent antes do setContentView
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_main);

        // Inicializar o mapa
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);

        // Centralizar em uma posição padrão
        GeoPoint startPoint = new GeoPoint(-16.1833, -57.6667);
        map.getController().setZoom(14.0);
        map.getController().setCenter(startPoint);

        // Firebase
        firestore = FirebaseFirestore.getInstance();

        // Buscar dados
        buscarPontosDoFirebase();
    }

    private void buscarPontosDoFirebase() {
        firestore.collection("enderecos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        double lat = doc.getDouble("latitude");
                        double lon = doc.getDouble("longitude");

                        GeoPoint ponto = new GeoPoint(lat, lon);
                        adicionarMarcador(ponto);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("FIREBASE", "Erro ao buscar pontos", e)
                );
    }

    private void adicionarMarcador(GeoPoint ponto) {
        Marker marker = new Marker(map);
        marker.setPosition(ponto);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Ponto no Firebase");
        map.getOverlays().add(marker);
        map.invalidate(); // Atualiza o mapa
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
