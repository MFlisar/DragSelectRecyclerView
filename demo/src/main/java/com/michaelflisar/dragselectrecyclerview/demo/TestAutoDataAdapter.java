package com.michaelflisar.dragselectrecyclerview.demo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;

/**
 * Created by flisar on 03.03.2017.
 */

public class TestAutoDataAdapter extends RecyclerView.Adapter<TestAutoDataAdapter.ViewHolder>
{

    private int mDataSize;
    private Context mContext;
    private ItemClickListener mClickListener;

    private HashSet<Integer> mSelected;

    public TestAutoDataAdapter(Context context, int size)
    {
        mContext = context;
        mDataSize = size;
        mSelected = new HashSet<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.test_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.tvText.setText(String.valueOf(position));
        if (mSelected.contains(position))
            holder.tvText.setBackgroundColor(Color.RED);
        else
            holder.tvText.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount()
    {
        return mDataSize;
    }

    // ----------------------
    // Selection
    // ----------------------

    public void select(int pos, boolean selected)
    {
        if (selected)
            mSelected.add(pos);
        else
            mSelected.remove(pos);
        notifyItemChanged(pos);
    }

    public void selectRange(int from, int to, boolean selected)
    {
        for (int i = from; i <= to; i++)
        {
            if (selected)
                mSelected.add(i);
            else
                mSelected.remove(i);
        }
        notifyItemRangeChanged(from, to - from + 1);
    }

    public void deselectAll()
    {
        // this is not beautiful...
        mSelected.clear();
        notifyDataSetChanged();
    }

    public int getCountSelected()
    {
        return mSelected.size();
    }

    // ----------------------
    // Click Listener
    // ----------------------

    public void setClickListener(ItemClickListener itemClickListener)
    {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }

    // ----------------------
    // ViewHolder
    // ----------------------

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        public TextView tvText;

        public ViewHolder(View itemView)
        {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view)
        {
            if (mClickListener != null)
                return mClickListener.onItemLongClick(view, getAdapterPosition());
            return false;
        }
    }
}
