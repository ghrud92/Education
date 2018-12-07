package com.hogle.education.Fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hogle.education.Listeners.MapFragmentEventListener;
import com.hogle.education.Model.Code;
import com.hogle.education.Model.Map;
import com.hogle.education.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment {
    private final int UP = 0;
    private final int RIGHT = 1;
    private final int DOWN = 2;
    private final int LEFT = 3;

    @BindView(R.id.map_layout) RelativeLayout layout;
    @BindView(R.id.map) ImageView mapImage;
    @BindView(R.id.car) ImageView carImage;
    @BindView(R.id.flag) ImageView flagImage;
    @BindView(R.id.map_spinner) Spinner roundSpinner;
    @BindView(R.id.result) TextView resultView;

    @BindArray(R.array.car_x) int []initialCarX;
    @BindArray(R.array.car_y) int []initialCarY;
    @BindArray(R.array.flag_x) int []flagX;
    @BindArray(R.array.flag_y) int []flagY;

    private Map mapData;
    private int carX, carY, direction, round;
    private MapFragmentEventListener listener;
    private int []rockX;
    private int []rockY;
    private ArrayList<View> rockViews;
    private boolean isSuccess;

    public void setListener(MapFragmentEventListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        rockViews = new ArrayList<>();

        return view;
    }

    @OnClick(R.id.start_btn)
    void startClicked (View view) {
        initializing();
    }

    private void initializing() {
        isSuccess = true;
        resultView.setVisibility(View.VISIBLE);
        direction = UP;
        mapData = new Map(mapImage.getLeft(), mapImage.getTop(), mapImage.getWidth());
        List<ObjectAnimator> animatorList = new LinkedList<>();
        for (View view : rockViews) {   // 바위 초기화
            ((ViewGroup) view.getParent ()).removeView (view);
        }
        rockViews.clear();

        //라운드 채크
        round = roundSpinner.getSelectedItemPosition();
        switch (round) {
            case 0:
                rockX = getResources().getIntArray(R.array.rock_1_x);
                rockY = getResources().getIntArray(R.array.rock_1_y);
                break;
            case 1:
                rockX = getResources().getIntArray(R.array.rock_2_x);
                rockY = getResources().getIntArray(R.array.rock_2_y);
                break;
            case 2:
                rockX = getResources().getIntArray(R.array.rock_3_x);
                rockY = getResources().getIntArray(R.array.rock_3_y);
                break;
        }

        // 성공여부 숨기기
        ObjectAnimator resultPivot = ObjectAnimator
                .ofFloat(resultView, "rotationX", 90, 90)
                .setDuration(200);
        animatorList.add(resultPivot);

        // 자동차 위치 선정
        carX = initialCarX[round];
        carY = initialCarY[round];
        ObjectAnimator carStartX = ObjectAnimator
                .ofFloat(carImage, "x", mapData.getX(initialCarX[round]), mapData.getX(initialCarX[round]))
                .setDuration(200);
        ObjectAnimator carStartY = ObjectAnimator
                .ofFloat(carImage, "y", mapData.getY(initialCarY[round]), mapData.getY(initialCarY[round]))
                .setDuration(200);
        ObjectAnimator carRotation = ObjectAnimator
                .ofFloat(carImage, "rotation", 0, 0);
        animatorList.add(carStartX);
        animatorList.add(carStartY);
        animatorList.add(carRotation);

        // 목표지점 위치 선정
        ObjectAnimator flagStartX = ObjectAnimator
                .ofFloat(flagImage, "x", mapData.getX(flagX[round]), mapData.getX(flagX[round]))
                .setDuration(200);
        ObjectAnimator flagStartY = ObjectAnimator
                .ofFloat(flagImage, "y", mapData.getY(flagY[round]), mapData.getY(flagY[round]))
                .setDuration(200);
        animatorList.add(flagStartX);
        animatorList.add(flagStartY);

        // 장애물 위치 선정
        for (int i = 0; i < rockX.length; i++) {
            ImageView rock = new ImageView(getActivity());
            rock.setImageResource(R.drawable.rock);
            rock.setLayoutParams(new RelativeLayout.LayoutParams(mapData.getCellLength(), mapData.getCellLength()));
            layout.addView(rock);
            rockViews.add(rock);
            ObjectAnimator rockMoveX = ObjectAnimator
                    .ofFloat(rock, "x", mapData.getX(rockX[i]), mapData.getX(rockX[i]))
                    .setDuration(200);
            ObjectAnimator rockMoveY = ObjectAnimator
                    .ofFloat(rock, "y", mapData.getY(rockY[i]), mapData.getY(rockY[i]))
                    .setDuration(200);
            animatorList.add(rockMoveX);
            animatorList.add(rockMoveY);
        }

        // 실행
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether((List) animatorList);
        animatorSet.start();
        carImage.setVisibility(View.VISIBLE);
        flagImage.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.action_btn)
    void actionClicked(View view) {
        // 위치 초기화
        initializing();

        // 코드 옮기기
        ArrayList<Code> codeList = listener.getCodeList();
        List<ObjectAnimator> animatorList = new LinkedList<>();
        for (Code code : codeList) {
            if (isOutside() || !isSuccess)
                break;
            ObjectAnimator objectAnimator = null;
            switch (code.getCode()) {
                case Code.FORWARD:
                    objectAnimator = goForward();
                    break;
                case Code.LEFT:
                case Code.RIGHT:
                    objectAnimator = turn(code.getCode());
                    break;
                case Code.IF:
                    Code result;
                    if (isRockInFront())
                        result = code.getTrueCode();
                    else
                        result = code.getFalseCode();
                    if (result.getCode() == Code.FORWARD)
                        objectAnimator = goForward();
                    else
                        objectAnimator = turn(code.getCode());
                    break;
            }
            if (objectAnimator != null) {
                objectAnimator.setDuration(1000);
                animatorList.add(objectAnimator);
            }
        }

        if (carX != flagX[round] || carY != flagY[round])
            isSuccess = false;

        // 성공 표시
        if (isSuccess) {
            resultView.setText("성공");
        } else {
            resultView.setText("실패");
        }
        ObjectAnimator resultPivot = ObjectAnimator
                .ofFloat(resultView, "rotationX", 0, 0)
                .setDuration(200);
        animatorList.add(resultPivot);

        // 코드 실행
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially((List) animatorList);
        animatorSet.start();
    }

    private ObjectAnimator goForward() {
        if (isRockInFront()) {
            isSuccess = false;
            return null;
        }
        ObjectAnimator objectAnimator = null;
        switch (direction) {
            case UP:
                carY--;
                objectAnimator = ObjectAnimator.ofFloat(carImage, "y", mapData.getY(carY));
                break;
            case RIGHT:
                carX++;
                objectAnimator = ObjectAnimator.ofFloat(carImage, "x", mapData.getX(carX));
                break;
            case DOWN:
                carY++;
                objectAnimator = ObjectAnimator.ofFloat(carImage, "y", mapData.getY(carY));
                break;
            case LEFT:
                carX--;
                objectAnimator = ObjectAnimator.ofFloat(carImage, "x", mapData.getX(carX));
                break;
        }
        return objectAnimator;
    }

    private ObjectAnimator turn (int rotation) {
        ObjectAnimator objectAnimator;
        int temp = direction;
        if (rotation == Code.LEFT) {
            direction = direction > 0 ? direction - 1 : 3;
            if (temp == 0) {
                objectAnimator = ObjectAnimator.ofFloat(carImage, "rotation", 360, 270);
            } else {
                objectAnimator = ObjectAnimator.ofFloat(carImage, "rotation", temp * 90, direction * 90);
            }
        } else {
            direction = (direction + 1) % 4;
            if (direction == 0) {
                objectAnimator = ObjectAnimator.ofFloat(carImage, "rotation", 270, 360);
            } else {
                objectAnimator = ObjectAnimator.ofFloat(carImage, "rotation", temp * 90, direction * 90);
            }
        }
        return objectAnimator;
    }

    private boolean isOutside () {
        return carX < 0 || carX > 4 || carY < 0 || carY > 4;
    }

    private boolean isProblemInFront () {
        return isRockInFront() || isWallInFront();
    }

    private boolean isRockInFront () {
        boolean result = false;
        int x = carX;
        int y = carY;
        switch (direction) {
            case UP:
                y--;
                break;
            case RIGHT:
                x++;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
        }
        for (int i = 0; i < rockX.length; i++) {
            if (rockX[i] == x && rockY[i] == y)
                result = true;
        }
        return result;
    }

    private boolean isWallInFront () {
        boolean result = false;
        int x = carX;
        int y = carY;
        switch (direction) {
            case UP:
                y--;
                break;
            case RIGHT:
                x++;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
        }
        if (x < 0 || x > 4 || y < 0 || y > 4)
            result = true;
        return result;
    }
}
