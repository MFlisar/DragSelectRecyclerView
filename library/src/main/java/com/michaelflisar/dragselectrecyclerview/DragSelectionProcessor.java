package com.michaelflisar.dragselectrecyclerview;

import java.util.HashSet;

/**
 * Created by Michael on 04.03.2017.
 */

public class DragSelectionProcessor implements DragSelectTouchListener.OnAdvancedDragSelectListener {

    /**
     *  Different existing selection modes
     */
    public static enum Mode
    {
        /**
         * simply selects each item you go by and unselects on move back
         */
        Simple,
        /**
         * toggles each items original state, reverts to the original state on move back
         */
        ToggleAndUndo,
        /**
         * toggles the first item and applies the same state to each item you go by and applies inverted state on move back
         */
        FirstItemDependent,
        /**
         * toggles the item and applies the same state to each item you go by and reverts to the original state on move back
         */
        FirstItemDependentToggleAndUndo
    }

    private Mode mMode;
    private ISelectionHandler mSelectionHandler;
    private HashSet<Integer> mOriginalSelection;
    private Boolean mFirstWasSelected;

    public DragSelectionProcessor(Mode mode, ISelectionHandler selectionHandler)
    {
        mMode = mode;
        mSelectionHandler = selectionHandler;
    }

    public void setMode(Mode mode)
    {
        mMode = mode;
    }

    @Override
    public void onSelectionStarted(int start)
    {
        switch (mMode)
        {
            case Simple:
                mSelectionHandler.updateSelection(start, true);
                break;
            case ToggleAndUndo:
                mOriginalSelection = new HashSet<>(mSelectionHandler.getSelection());
                mSelectionHandler.updateSelection(start, !mOriginalSelection.contains(start));
                break;
            case FirstItemDependent:
                mFirstWasSelected = mSelectionHandler.getSelection().contains(start);
                mSelectionHandler.updateSelection(start, !mFirstWasSelected);
                break;
            case FirstItemDependentToggleAndUndo:
                mOriginalSelection = new HashSet<>(mSelectionHandler.getSelection());
                mFirstWasSelected = mOriginalSelection.contains(start);
                mSelectionHandler.updateSelection(start, !mOriginalSelection.contains(start));
                break;
        }
    }

    @Override
    public void onSelectionFinished(int end)
    {
        switch (mMode)
        {
            case Simple:
                break;
            case ToggleAndUndo:
                mOriginalSelection = null;
                break;
            case FirstItemDependent:
                mFirstWasSelected = null;
                break;
            case FirstItemDependentToggleAndUndo:
                mOriginalSelection = null;
                mFirstWasSelected = null;
                break;
        }
    }

    @Override
    public void onSelectChange(int start, int end, boolean isSelected)
    {
        switch (mMode)
        {
            case Simple:
            {
                mSelectionHandler.updateSelection(start, end, isSelected);
                break;
            }
            case ToggleAndUndo:
            {
                for (int i = start; i <= end; i++)
                    mSelectionHandler.updateSelection(i, isSelected ? !mOriginalSelection.contains(i) :  mOriginalSelection.contains(i));
                break;
            }
            case FirstItemDependent:
            {
                for (int i = start; i <= end; i++)
                    mSelectionHandler.updateSelection(i, isSelected ? !mFirstWasSelected :  mFirstWasSelected);
                break;
            }
            case FirstItemDependentToggleAndUndo:
            {
                for (int i = start; i <= end; i++)
                    mSelectionHandler.updateSelection(i, isSelected ? !mFirstWasSelected :  mOriginalSelection.contains(i));
                break;
            }
        }
    }

    public interface ISelectionHandler
    {
        /*
         * Return the currently selected items => can be ignored for {@link Mode.Simple} and {@link Mode.FirstItemDependent}
         */
        HashSet<Integer> getSelection();
        /*
        * update your adapter and select/unselect the passed index
        */
        void updateSelection(int index, boolean isSelected);
        /*
       * update your adapter and select select/unselect the passed index range
       */
        void updateSelection(int start, int end, boolean isSelected);
    }
}
