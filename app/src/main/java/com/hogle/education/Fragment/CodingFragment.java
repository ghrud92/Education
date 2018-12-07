package com.hogle.education.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hogle.education.Adapter.CodingRecyclerViewAdapter;
import com.hogle.education.Listeners.CodingListEventListener;
import com.hogle.education.Model.Code;
import com.hogle.education.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CodingFragment extends Fragment {
    @BindView(R.id.code_list) RecyclerView codingRecyclerView;

    private ArrayList<Code> codingList;
    private CodingRecyclerViewAdapter codingAdapter;
    private int listNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coding, container, false);
        ButterKnife.bind(this, view);
        initializing();

        return view;
    }

    private void initializing() {
        codingList = new ArrayList<>();
        listNum = -1;

        codingAdapter = new CodingRecyclerViewAdapter(getActivity(), codingList);
        codingAdapter.setCodingListEventListener(new CodingListEventListener() {
            @Override
            public void onClickCode(int index) {
                listNum = index;
            }
        });

        LinearLayoutManager codingLayoutManager = new LinearLayoutManager(getActivity());
        codingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        codingRecyclerView.setHasFixedSize(true);
        codingRecyclerView.setAdapter(codingAdapter);
        codingRecyclerView.setLayoutManager(codingLayoutManager);
    }

    public void addCode (Code code) {
        if (listNum == -1) {
            codingList.add(code);
        } else if (codingList.get(listNum).getCode() == Code.LOOP && code.getCode() != Code.LOOP) {
            // 루프안에 루프빼고 다 추가 가능
            ArrayList<Code> innerList = codingList.get(listNum).getLoopList();
            innerList.add(code);
        }
        if (listNum != -1) {
            codingList.get(listNum).setChecked(false);
            listNum = -1;
        }
        codingAdapter.notifyDataSetChanged();
    }

    public ArrayList<Code> getResultList() {
        ArrayList<Code> resultList = new ArrayList<>();
        for (Code code : codingList) {
            if (code.getCode() == Code.LOOP) {
                for (int j = 0; j < code.getLoopCount(); j++)
                    for (Code inner : code.getLoopList())
                        resultList.add(inner);
            } else {
                // 조건문은 실제 코드가 실행될때 조건 채크해서 동작
                resultList.add(code);
            }
        }
        return resultList;
    }
}
