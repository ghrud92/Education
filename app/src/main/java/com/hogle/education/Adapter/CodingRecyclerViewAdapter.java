package com.hogle.education.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hogle.education.Listeners.CodingListEventListener;
import com.hogle.education.Model.Code;
import com.hogle.education.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hogle.education.Model.Code.IF;
import static com.hogle.education.Model.Code.LOOP;
import static com.hogle.education.Model.Code.NOTHING;


public class CodingRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int STANDARD_CODE = 1;
    private static final int IF_CODE = 2;
    private static final int LOOP_CODE = 3;

    private Context context;
    private ArrayList<Code> list;
    private CodingListEventListener codingListEventListener;

    public CodingRecyclerViewAdapter(Context context, ArrayList<Code> codingList) {
        this.context = context;
        list = codingList;
    }

    public void setCodingListEventListener(CodingListEventListener listener) {
        codingListEventListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {    // i = viewType
        // 뷰타입에 따라 뷰를 다르게 띄운다.
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch(i) {
            case STANDARD_CODE:
                view = LayoutInflater.from(context).inflate(R.layout.item_standard_coding, viewGroup, false);
                viewHolder = new CodingViewHolder(view);
                break;
            case IF_CODE:
                view = LayoutInflater.from(context).inflate(R.layout.item_if_coding, viewGroup, false);
                viewHolder = new IfViewHolder(view);
                break;
            case LOOP_CODE:
                view = LayoutInflater.from(context).inflate(R.layout.item_loop_coding, viewGroup, false);
                viewHolder = new LoopViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {  // i = position
        final Code code = list.get(i);
        // 뷰타입에 따라 뷰를 바인드한다.
        switch (getItemViewType(i)) {
            case IF_CODE:
                IfViewHolder ifViewHolder = (IfViewHolder) holder;
                if (code.getIfCondition() != 0) {   // 설정한게 없다면 0
                    ifViewHolder.conditionSpinner.setSelection(code.getIfCondition() - 100);
                }
                ifViewHolder.conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        code.setIfCondition(position + 100);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                ifViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(code);
                        codingListEventListener.onClickCode(-1);
                        notifyDataSetChanged();
                    }
                });
                // true와 false 동작
                if (code.getTrueCode().getCode() == NOTHING)
                    ifViewHolder.actionTrue.setText("동작");
                else
                    ifViewHolder.actionTrue.setText(code.getTrueCode().getStringCode());
                if (code.getFalseCode().getCode() == NOTHING)
                    ifViewHolder.actionFalse.setText("동작");
                else
                    ifViewHolder.actionFalse.setText(code.getFalseCode().getStringCode());
                break;
            case LOOP_CODE:
                final LoopViewHolder loopViewHolder = (LoopViewHolder) holder;
                if (code.getLoopCount() == 0)
                    code.setLoopCount(2);
                loopViewHolder.loopSpinner.setSelection(code.getLoopCount() - 2);
                loopViewHolder.loopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        code.setLoopCount(position + 2);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                loopViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(code);
                        codingListEventListener.onClickCode(-1);
                        notifyDataSetChanged();
                    }
                });
                // loop 리사이클러 안에 코드 넣는 방법.
                if (code.isChecked()) {
                    loopViewHolder.addButton.setImageResource(R.drawable.ic_check);
                } else {
                    loopViewHolder.addButton.setImageResource(R.drawable.ic_add_black);
                }
                loopViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (code.isChecked()) {
                            code.setChecked(false);
                            loopViewHolder.addButton.setImageResource(R.drawable.ic_add_black);
                            codingListEventListener.onClickCode(-1);
                        } else {
                            code.setChecked(true);
                            loopViewHolder.addButton.setImageResource(R.drawable.ic_check);
                            codingListEventListener.onClickCode(i);
                        }
                    }
                });
                if (code.getAdapter() == null)
                    code.setAdapter(new NestedLoopRecyclerViewAdapter(context, code.getLoopList()));
                loopViewHolder.innerCodingRecycler.setHasFixedSize(true);
                loopViewHolder.innerCodingRecycler.setAdapter(code.getAdapter());
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                loopViewHolder.innerCodingRecycler.setLayoutManager(manager);
                break;
            default:
                CodingViewHolder codingViewHolder = (CodingViewHolder) holder;
                codingViewHolder.content.setText(code.getStringCode());
                codingViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(code);
                        codingListEventListener.onClickCode(-1);
                        notifyDataSetChanged();
                    }
                });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        switch (list.get(position).getCode()) {
            case IF:
                type = IF_CODE;
                break;
            case LOOP:
                type = LOOP_CODE;
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

    public static class CodingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.coding_content) TextView content;
        @BindView(R.id.coding_delete) ImageView deleteButton;

        public CodingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class IfViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.condition_spinner) Spinner conditionSpinner;
        @BindView(R.id.if_delete) ImageView deleteButton;
        @BindView(R.id.condition_true) TextView actionTrue;
        @BindView(R.id.condition_false) TextView actionFalse;

        public IfViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class LoopViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.loop_view) RelativeLayout loopView;
        @BindView(R.id.loop_spinner) Spinner loopSpinner;
        @BindView(R.id.loop_delete) ImageView deleteButton;
        @BindView(R.id.loop_add) ImageView addButton;
        @BindView(R.id.inner_coding_list) RecyclerView innerCodingRecycler;

        public LoopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
