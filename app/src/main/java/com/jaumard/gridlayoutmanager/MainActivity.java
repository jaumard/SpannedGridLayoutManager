package com.jaumard.gridlayoutmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaumard.gridlayoutmanager.SpannedGridLayoutManager.SpanInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final List<Integer> data = new ArrayList<>();

        SpannedGridLayoutManager gridLayoutManager = new SpannedGridLayoutManager(new SpannedGridLayoutManager.GridSpanLookup() {
            @Override
            public SpanInfo getSpanInfo(int position) {
                SpanInfo spanInfo = new SpanInfo(1, 1);
                if (data.get(position) == 1 || data.get(position) == 4) {
                    spanInfo.columnSpan = 2;
                    spanInfo.rowSpan = 2;
                } else if (data.get(position) == 0) {
                    spanInfo.columnSpan = 2;
                } else if (data.get(position) == 3) {
                    spanInfo.rowSpan = 2;
                }

                return spanInfo;
            }
        }, 2, 1f); //FIXME Toggle between 2 and 5, 2 will make crash on scroll and 5 will show overlapping

        recyclerView.setLayoutManager(gridLayoutManager);

        for (int i = 0; i < 20; i++) {
            data.add(i);
        }
        final MyAdapter myAdapter = new MyAdapter(data);
        recyclerView.setAdapter(myAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                myAdapter.onItemMove(viewHolder.getLayoutPosition(), target.getLayoutPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setHasFixedSize(true);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            GridLayoutManager.LayoutParams layoutParams = new GridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            float margin = DimensionUtils.convertDpToPixel(5);
            layoutParams.setMargins((int) margin, (int) margin, (int) margin, (int) margin);
            itemView.setLayoutParams(layoutParams);
            textView.setBackgroundColor(Color.RED);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {
        List<Integer> data;

        public MyAdapter(List<Integer> data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(new TextView(getApplicationContext()));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MyViewHolder) holder).setText(data.get(position) + "");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(data, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(data, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    }

    public interface ItemTouchHelperAdapter {

        boolean onItemMove(int fromPosition, int toPosition);

    }
}
