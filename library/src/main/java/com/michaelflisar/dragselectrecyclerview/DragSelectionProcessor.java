package com.michaelflisar.dragselectrecyclerview;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Michael on 04.03.2017.
 */

public class DragSelectionProcessor implements DragSelectTouchListener.OnAdvancedDragSelectListener {

    /**
     *  Different existing selection modes
     */
    public enum Mode
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
    private ISelectionStartFinishedListener mStartFinishedListener;
    private HashSet<Integer> mOriginalSelection;
    private boolean mFirstWasSelected;

    /**
     * @param selectionHandler the handler that takes care to handle the selection events
     */
    public DragSelectionProcessor(ISelectionHandler selectionHandler)
    {
        mMode = Mode.Simple;
        mSelectionHandler = selectionHandler;
        mStartFinishedListener = null;
    }

    /**
     * @param mode the mode in which the selection events should be processed
     * @return this
     */
    public DragSelectionProcessor withMode(Mode mode)
    {
        mMode = mode;
        return this;
    }
    
    /**
     * @param startFinishedListener a listener that get's notified when the drag selection is started or finished
     * @return this
     */
    public DragSelectionProcessor withStartFinishedListener(ISelectionStartFinishedListener startFinishedListener)
    {
        mStartFinishedListener = startFinishedListener;
        return this;
    }

    @Override
    public void onSelectionStarted(int start)
    {
        mOriginalSelection = new HashSet<>();
        Set<Integer> selected = mSelectionHandler.getSelection();
        if (selected != null)
            mOriginalSelection.addAll(selected);
        mFirstWasSelected = mOriginalSelection.contains(start);

        switch (mMode)
        {
            case Simple:
            {
                mSelectionHandler.updateSelection(start, start, true);
                break;
            }
            case ToggleAndUndo:
            {
                mSelectionHandler.updateSelection(start, start, !mOriginalSelection.contains(start));
                break;
            }
            case FirstItemDependent:
            {
                mSelectionHandler.updateSelection(start, start, !mFirstWasSelected);
                break;
            }
            case FirstItemDependentToggleAndUndo:
            {
                mSelectionHandler.updateSelection(start, start, !mFirstWasSelected);
                break;
            }
        }
        if (mStartFinishedListener != null)
            mStartFinishedListener.onSelectionStarted(start, mFirstWasSelected);
    }

    @Override
    public void onSelectionFinished(int end)
    {
        mOriginalSelection = null;

        if (mStartFinishedListener != null)
            mStartFinishedListener.onSelectionFinished(end);
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
                    mSelectionHandler.updateSelection(i, i, isSelected ? !mOriginalSelection.contains(i) :  mOriginalSelection.contains(i));
                break;
            }
            case FirstItemDependent:
            {
                for (int i = start; i <= end; i++)
                    mSelectionHandler.updateSelection(i, i, isSelected ? !mFirstWasSelected :  mFirstWasSelected);
                break;
            }
            case FirstItemDependentToggleAndUndo:
            {
                for (int i = start; i <= end; i++)
                    mSelectionHandler.updateSelection(i, i, isSelected ? !mFirstWasSelected :  mOriginalSelection.contains(i));
                break;
            }
        }
    }

    public interface ISelectionHandler
    {
        /**
         * Return the currently selected items => can be ignored for {@link Mode#Simple} and {@link Mode#FirstItemDependent}
         */
        Set<Integer> getSelection();

        /**
        * update your adapter and select select/unselect the passed index range, you be get a single for all modes but {@link Mode#Simple}
        *
        * @param start      the first item of the range who's selection state changed
        * @param end         the last item of the range who's selection state changed
        * @param isSelected      true, if the range should be selected, false otherwise
        */
        void updateSelection(int start, int end, boolean isSelected);
    }

    public interface ISelectionStartFinishedListener
    {
        /**
         * @param start      the item on which the drag selection was started at
         * @param originalSelectionState the original selection state
         */
        void onSelectionStarted(int start, boolean originalSelectionState);

        /**
         * @param end      the item on which the drag selection was finished at
         */
        void onSelectionFinished(int end);
    }
}
