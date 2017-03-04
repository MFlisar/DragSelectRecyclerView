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
import com.michaelflisar.dragselectrecyclerview.DragSelectionProcessor;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity
{
    private DragSelectionProcessor.Mode mMode = DragSelectionProcessor.Mode.Simple;

    private Toolbar mToolbar;
    private DragSelectTouchListener mDragSelectTouchListener;
    private TestAutoDataAdapter mAdapter;

    private DragSelectionProcessor mDragSelectionProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("DragSelectRecyclerView");
        setSupportActionBar(mToolbar);

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
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                mDragSelectTouchListener.startDragSelection(position);
                return true;
            }
        });

        // 2) Add the DragSelectListener
        mDragSelectionProcessor = new DragSelectionProcessor(mMode, new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public HashSet<Integer> getSelection() {
                return mAdapter.getSelection();
            }

            @Override
            public void updateSelection(int index, boolean isSelected) {
                mAdapter.select(index, isSelected);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected) {
                mAdapter.selectRange(start, end, isSelected);
            }
        });
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(mDragSelectionProcessor);
        updateSelectionListener();
        rvData.addOnItemTouchListener(mDragSelectTouchListener);
    }

    // ---------------------
    // Selection Listener
    // ---------------------

    private void updateSelectionListener()
    {
        mDragSelectionProcessor.setMode(mMode);
        mToolbar.setSubtitle("Mode: " + mMode.name());
    }

    // ---------------------
    // Menu
    // ---------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_clear)
            mAdapter.deselectAll();
        if (item.getItemId() == R.id.menu_select_all)
            mAdapter.selectAll();
        else if (item.getItemId() == R.id.mode_simple)
        {
            mMode = DragSelectionProcessor.Mode.Simple;
            updateSelectionListener();
        }
        else if (item.getItemId() == R.id.mode_toggle)
        {
            mMode = DragSelectionProcessor.Mode.ToggleAndUndo;
            updateSelectionListener();
        }
        else if (item.getItemId() == R.id.mode_first_item_dependant)
        {
            mMode = DragSelectionProcessor.Mode.FirstItemDependent;
            updateSelectionListener();
        }
        else if (item.getItemId() == R.id.mode_first_item_dependant_toggle)
        {
            mMode = DragSelectionProcessor.Mode.FirstItemDependentToggleAndUndo;
            updateSelectionListener();
        }
        return true;
    }
}