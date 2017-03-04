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

import java.util.HashSet;

public class MainActivity extends AppCompatActivity
{
    enum Mode
    {
        Simple,
        AdvancedToggling,
        AdvancedFirstItemDependent
    }

    private Mode mMode = Mode.Simple;

    private Toolbar mToolbar;
    private DragSelectTouchListener mDragSelectTouchListener;
    private TestAutoDataAdapter mAdapter;

    private DragSelectTouchListener.OnDragSelectListener mSimpleSelectionListener;
    private DragSelectTouchListener.OnDragSelectListener mAdvancedTogglingSelectionListener;
    private DragSelectTouchListener.OnDragSelectListener mAdvancedFirstItemDependantSelectionListener;

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
                mDragSelectTouchListener.startDragSelection(position);
                // of course we mark the position as selected as well (depending on the selected mode we do something else)
                switch (mMode)
                {
                    case Simple:
                        mAdapter.select(position, true);
                        break;
                    case AdvancedToggling:
                    case AdvancedFirstItemDependent:
                        mAdapter.toggleSelection(position);
                        break;
                }

                return true;
            }
        });

        // 2) Add the DragSelectListener
        mDragSelectTouchListener = new DragSelectTouchListener();
        initSelectionListeners();
        updateSelectionListener();
        rvData.addOnItemTouchListener(mDragSelectTouchListener);
    }

    // ---------------------
    // Selection Listener - init + update
    // ---------------------

    private void initSelectionListeners()
    {
        /*
        * this one just updates the selection as google photos does it, this means:
        * dragging away from the initial position is selecting items, dragging back
        * to the initial item is deselecting items => the original state is ignored
         */
        mSimpleSelectionListener = new DragSelectTouchListener.OnDragSelectListener()
        {
            @Override
            public void onSelectChange(int start, int end, boolean isSelected)
            {
                // update your selection
                // range is inclusive start/end positions: [start, end]
                mAdapter.selectRange(start, end, isSelected);
            }
        };
        /*
        * this is an advanced solution, it makes sure, that dragging away from the initial position does
        * toggle the selection state of the newly dragged over items, dragging back does revert the toggeling to
        * the original state
         */
        mAdvancedTogglingSelectionListener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {

            private HashSet<Integer> originalSelection;

            @Override
            public void onSelectChange(int start, int end, boolean isSelected) {
                // update your selection
                // range is inclusive start/end positions: [start, end]
                for (int i = start; i <= end; i++)
                {
                    if (isSelected)
                        mAdapter.select(i, !originalSelection.contains(i));
                    else
                        mAdapter.select(i, originalSelection.contains(i));
                }
            }

            @Override
            public void onSelectionStarted(int start) {
                // we save a copy of the initial selection
                originalSelection = new HashSet<>(mAdapter.getSelection());
            }

            @Override
            public void onSelectionFinished(int end) {
                // we reset the copy of the initial selection
                originalSelection = null;
            }
        };
        mAdvancedFirstItemDependantSelectionListener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {

            private HashSet<Integer> originalSelection;
            private boolean mFirstWasSelected;

            @Override
            public void onSelectChange(int start, int end, boolean isSelected) {
                // update your selection
                // range is inclusive start/end positions: [start, end]
                for (int i = start; i <= end; i++)
                {
                    if (isSelected)
                        mAdapter.select(i, !mFirstWasSelected);
                    else
                        mAdapter.select(i, mFirstWasSelected);
                }
            }

            @Override
            public void onSelectionStarted(int start) {
                // we save a copy of the initial selection and find out the selection state of the first item
                originalSelection = new HashSet<>(mAdapter.getSelection());
                mFirstWasSelected = originalSelection.contains(start);
            }

            @Override
            public void onSelectionFinished(int end) {
                // we reset the copy of the initial selection
                originalSelection = null;
            }
        };
    }

    private void updateSelectionListener()
    {
        switch (mMode)
        {
            case Simple:
                mDragSelectTouchListener
                        .withSelectListener(mSimpleSelectionListener);
                break;
            case AdvancedToggling:
                mDragSelectTouchListener
                        .withSelectListener(mAdvancedTogglingSelectionListener);
                break;
            case AdvancedFirstItemDependent:
                mDragSelectTouchListener
                        .withSelectListener(mAdvancedFirstItemDependantSelectionListener);
                break;
        }

        mToolbar.setSubtitle("Mode: " + mMode.name());
    }

    // ---------------------
    // Menu
    // ---------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear)
            mAdapter.deselectAll();
        if (item.getItemId() == R.id.menu_select_all)
            mAdapter.selectAll();
        else if (item.getItemId() == R.id.mode_simple)
        {
            mMode = Mode.Simple;
            updateSelectionListener();
        }
        else if (item.getItemId() == R.id.mode_advanced_toggling)
        {
            mMode = Mode.AdvancedToggling;
            updateSelectionListener();
        }
        else if (item.getItemId() == R.id.mode_advanced_first_item_dependant)
        {
            mMode = Mode.AdvancedFirstItemDependent;
            updateSelectionListener();
        }
        return true;
    }
}