package com.uas.facite.adoptaunbache;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.navigation.NavigationView;

public abstract class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navegacion;
    private ImageButton botonMenu;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        /*botonMenu = (ImageButton)findViewById(R.id.botonMenu);
        navegacion = (NavigationView)findViewById(R.id.nav_view);
        botonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawer.isDrawerOpen(Gravity.LEFT))
                    drawer.closeDrawer(Gravity.LEFT);
                else
                    drawer.openDrawer(Gravity.LEFT);
            }
        });*/
    }
}
