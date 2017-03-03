package com.michaelflisar.dragselectrecyclerview.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

public class MainActivity extends AppCompatActivity
{
    private DragSelectTouchListener mDragSelectTouchListener;
    private TestAutoDataAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Demo");
        setSupportActionBar(toolbar);

        // 1) Prepare the RecyclerView (init LayoutManager and set Adapter)
        RecyclerView rvData = (RecyclerView) findViewById(R.id.rvData);
        GridLayoutManager glm = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(glm);
        mAdapter = new TestAutoDataAdapter(this, 500);
        rvData.setAdapter(mAdapter);
        mAdapter.setClickListener(new TestAutoDataAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                mAdapter.toggleSelection(position);
            }

            @Override
            public boolean onItemLongClick(View view, int position)
            {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                mAdapter.select(position, true);
                mDragSelectTouchListener.startDragSelection(position);
                return true;
            }
        });

        // 2) Add the DragSelectListener
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(new DragSelectTouchListener.OnDragSelectListener()
                {
                    @Override
                    public void onSelectChange(int start, int end, boolean isSelected)
                    {
                        // update your selection
                        // range is inclusive start/end positions
                        mAdapter.selectRange(start, end, isSelected);
                    }
                });
        rvData.addOnItemTouchListener(mDragSelectTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear)
            mAdapter.deselectAll();
        return true;
    }
}
