package com.hogle.education.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hogle.education.Listeners.CodingListEventListener;
import com.hogle.education.Model.Code;
import com.hogle.education.R;

import java.util.ArrayList;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hogle.education.Model.Code.NOTHING;

public class NestedLoopRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int STANDARD_CODE = 1;
    private static final int IF_CODE = 2;

    private Context context;
    private ArrayList<Code> list;

    // 현재는 쓰이지 않는다.
    private CodingListEventListener codingListEventListener;

    public NestedLoopRecyclerViewAdapter(Context context, ArrayList<Code> codingList) {
        this.context = context;
        list = codingList;
    }

    public void setCodingListEventListener(CodingListEventListener listener) {
        codingListEventListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        if (i == STANDARD_CODE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_inner_standard_coding, viewGroup, false);
            viewHolder = new CodingRecyclerViewAdapter.CodingViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_inner_if_coding, viewGroup, false);
            viewHolder = new IfViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Code code = list.get(i);
        switch (getItemViewType(i)) {
            case STANDARD_CODE:
                CodingRecyclerViewAdapter.CodingViewHolder codingViewHolder = (CodingRecyclerViewAdapter.CodingViewHolder) viewHolder;
                codingViewHolder.content.setText(code.getStringCode());
                codingViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(code);
                        notifyDataSetChanged();
                    }
                });
                break;
            case IF_CODE:
                IfViewHolder ifViewHolder = (IfViewHolder) viewHolder;
                ifViewHolder.condition.setText(ifViewHolder.fences[code.getIfCondition() - 100]);
                ifViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(code);
                        notifyDataSetChanged();
                    }
                });
                if (code.getTrueCode().getCode() == NOTHING)
                    ifViewHolder.actionTrue.setText("동작");
                else
                    ifViewHolder.actionTrue.setText(code.getTrueCode().getStringCode());
                if (code.getFalseCode().getCode() == NOTHING)
                    ifViewHolder.actionFalse.setText("동작");
                else
                    ifViewHolder.actionFalse.setText(code.getFalseCode().getStringCode());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        switch (list.get(position).getCode()) {
            case Code.IF:
                type = IF_CODE;
                break;
            default:
                type = STANDARD_CODE;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class IfViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.condition) TextView condition;
        @BindView(R.id.if_delete) ImageView deleteButton;
        @BindView(R.id.condition_true) TextView actionTrue;
        @BindView(R.id.condition_false) TextView actionFalse;
        @BindArray(R.array.fences) String[] fences;

        public IfViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
