package com.hogle.education.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hogle.education.Adapter.ExercisePagerAdapter;
import com.hogle.education.Fragment.CodingFragment;
import com.hogle.education.Fragment.IfDialog;
import com.hogle.education.Fragment.MapFragment;
import com.hogle.education.Listeners.IfDialogListener;
import com.hogle.education.Listeners.MapFragmentEventListener;
import com.hogle.education.Model.Code;
import com.hogle.education.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseActivity extends AppCompatActivity {
    @BindView(R.id.app_bar) AppBarLayout appBar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.exercise_pager) ViewPager viewPager;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.fab_forward) FloatingActionButton forwardFab;
    @BindView(R.id.fab_left) FloatingActionButton leftFab;
    @BindView(R.id.fab_right) FloatingActionButton rightFab;
    @BindView(R.id.fab_if) FloatingActionButton ifFab;
    @BindView(R.id.fab_loop) FloatingActionButton loopFab;

    private MapFragment mapFragment;
    private CodingFragment codingFragment;
    private ExercisePagerAdapter adapter;
    private MapFragmentEventListener mapFragmentEventListener;
    private IfDialogListener ifDialogListener;
    private boolean isFabClicked;

    public static Intent newIntent(Context context) {
        return new Intent(context, ExerciseActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        ButterKnife.bind(this);

        setFragments();
        setOtherViews();
    }

    private void setFragments() {
        // fragment 생성
        mapFragment = new MapFragment();
        codingFragment = new CodingFragment();

        // listener 생성
        setListener();
        mapFragment.setListener(mapFragmentEventListener);

        // PagerAdapter 생성
        adapter = new ExercisePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapFragment, "지도");
        adapter.addFragment(codingFragment, "코딩");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        // map fragment
                        isFabClicked = false;
                        fab.setVisibility(View.GONE);
                        closeFabs();
                        break;
                    case 1:
                        // coding fragment
                        fab.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    private void setListener() {
        mapFragmentEventListener = new MapFragmentEventListener() {
            @Override
            public ArrayList<Code> getCodeList() {
                return codingFragment.getResultList();
            }
        };
        ifDialogListener = new IfDialogListener() {
            @Override
            public void sendCode(Code code) {
                codingFragment.addCode(code);
            }
        };
    }

    private void setOtherViews() {
        isFabClicked = false;
        fab.setVisibility(View.GONE);   // 처음엔 지도 fragment가 보일테니 fab를 없앤다.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFabClicked = !isFabClicked;
                if (isFabClicked)
                    openFabs();
                else
                    closeFabs();
            }
        });
        forwardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codingFragment.addCode(new Code(Code.FORWARD));
            }
        });
        leftFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codingFragment.addCode(new Code(Code.LEFT));
            }
        });
        rightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codingFragment.addCode(new Code(Code.RIGHT));
            }
        });
        ifFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IfDialog dialog = new IfDialog(ExerciseActivity.this);
                dialog.setListener(ifDialogListener);
                dialog.show();
            }
        });
        loopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codingFragment.addCode(new Code(Code.LOOP));
            }
        });
    }

    private void openFabs () {
        fab.setImageResource(R.drawable.ic_close_white_24dp);
        setFabsVisibility(View.VISIBLE);
    }

    private void closeFabs () {
        fab.setImageResource(R.drawable.ic_add_white);
        setFabsVisibility(View.GONE);
    }

    private void setFabsVisibility (int visibility) {
        forwardFab.setVisibility(visibility);
        leftFab.setVisibility(visibility);
        rightFab.setVisibility(visibility);
        ifFab.setVisibility(visibility);
        loopFab.setVisibility(visibility);
    }
}
