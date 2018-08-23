package com.example.harshsaini.meme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.harshsaini.meme.UI.ImagesFilters;

import java.util.List;

public class Filters extends AppCompatActivity {

    List<Integer>list;
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        gridView=(GridView)findViewById(R.id.gridView);
        list=ImagesFilters.getAllFilters();
        MasterListAdapter masterListAdapter=new MasterListAdapter(getApplicationContext(),list);

        gridView.setAdapter(masterListAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FaceGraphics.ide=list.get(position);
                finish();
            }
        });
    }
}
