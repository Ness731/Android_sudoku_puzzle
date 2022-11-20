package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    final MaterialButton buttons[][] = new MaterialButton[9][9];
    final int[] buttonID = new int[]{R.id.num1, R.id.num2, R.id.num3,
            R.id.num4, R.id.num5, R.id.num6, R.id.num7,
            R.id.num8, R.id.num9, R.id.cancel, R.id.confirm};
    final Context context = this;
    final int[][] groups = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    int[] margin = new int[4];
    BoardGenerator board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initContent();
    }

    private void initContent() {
        // 버튼이 화면을 넘어가지 않도록 자동 크기 조절
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        tableLayout.setShrinkAllColumns(true);
        initResetButton();
        createButton(tableLayout);
    }

    private void initResetButton() {
        final Button button = (Button) findViewById(R.id.resetButton);
        final String[] resetMessage = {"게임이 초기화되었습니다.", "초기화 취소"};
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.reset_dialog);

                Button reset_cancel = (Button) dialog.findViewById(R.id.reset_cancel);
                Button reset_confirm = (Button) dialog.findViewById(R.id.reset_confirm);

                reset_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toasty.info(context, resetMessage[1]).show();
                    }
                });
                reset_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initNumber();
                        dialog.dismiss();
                        Toasty.info(context, resetMessage[0]).show();
                    }
                });
                dialog.show();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void createButton(TableLayout tableLayout) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                View dialogButton = null;
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.number_pad);
                for (int i = 0; i < buttonID.length; i++) {
                    if (i < 9) {
                        dialogButton = (Button) dialog.findViewById(buttonID[i]);
                    } else {
                        dialogButton = (ImageButton) dialog.findViewById(buttonID[i]);
                    }
                    int finalI = i;
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Toasty.info(context, initMessage(finalI)).show();
                        }
                    });
                }
                dialog.show();
            }
        };

        // i=row, j=column을 의미함
        for (int row = 0; row < 9; row++) {
            final TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
            );
            tableRow.setPadding(0, -15, 0, -15);
            for (int col = 0; col < 9; col++) {
                board = new BoardGenerator();
                buttons[row][col] = new MaterialButton(context);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f);

                // 3개 단위로 margin을 크게 주는 함수
                setMargin(row, col);

                // setMargins(left, top, right, bottom)
                layoutParams.setMargins(margin[0], margin[1], margin[2], margin[3]);

                // board.get(i, j)는 int를 반환하고 setText는 String을 받기 때문에 String으로 형변환 필요
                String number = Integer.toString(board.get(row, col));
                setButtonDesign(row, col, number, layoutParams, listener);
                tableRow.addView(buttons[row][col]);
            }
            tableLayout.addView(tableRow);
        }
    }

    private void setAllEmptyButton() {
        for (int[] rows : groups) {
            for (int[] cols : groups) {
                setEmptyButton(rows, cols);
            }
        }
    }

    private void setEmptyButton(int[] rows, int[] cols) {
        HashMap<Integer, Integer> elements = new HashMap<>();
        Random rand = new Random();
        int randomCount = rand.nextInt(5) + 2; // 빈 블록 개수는 2~6개
        int randomIndex;
        int emptyRow, emptyCol;

        for (int row = 0; row < rows.length; row++) { // 3x3 영역의 좌표를 hashmap 형태로 변환
            for (int col = 0; col < cols.length; col++) {
                elements.put(row, col);
            }
        }
        // 빈 블록 좌표를 랜덤하게 선정
        Object[] keys = elements.keySet().toArray();
        for (int i = 0; i <= randomCount; i++) {
            randomIndex = rand.nextInt(elements.size());
            emptyRow = (int) keys[randomIndex];
            emptyCol = elements.get(keys[randomIndex]);
            elements.remove(emptyRow);
        }

    }

    private void initNumber() {
        BoardGenerator board = new BoardGenerator();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String number = Integer.toString(board.get(i, j));
                buttons[i][j].setText(number);
            }
        }
    }

    private void initMargin() {
        for (int i = 0; i < margin.length; i++)
            margin[i] = 2;
    }

    @SuppressLint("ResourceAsColor")
    private void setButtonDesign(int i, int j, String number, ViewGroup.LayoutParams layoutParams,
                                 View.OnClickListener listener) {
        buttons[i][j].setLayoutParams(layoutParams);
        buttons[i][j].setText(number);
        buttons[i][j].setTextSize(20);
        buttons[i][j].setTextColor(R.color.black);
        buttons[i][j].setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        buttons[i][j].setOnClickListener(listener);
        buttons[i][j].setCornerRadius(0);
    }

    private void setMargin(int i, int j) {
        // [0] = left, [1] = top, [2] = right, [3] = bottom)
        initMargin();
        if (j == 8) margin[2] = 20;
        if (j == 0) margin[0] = 20;
        if (i == 8) margin[3] = 20;
        if (i == 0) margin[1] = 20;
        if (j == 3 || j == 6) margin[0] = 10;
        if (i == 3 || i == 6) margin[1] = 10;
    }

    private String initMessage(int i) {
        String msg = null;
        if (i < 9)
            msg = (i + 1) + "번";
        if (buttonID[i] == R.id.cancel)
            msg = "취소";
        if (buttonID[i] == R.id.confirm)
            msg = "확인";
        msg += " 버튼이 클릭되었습니다.";
        // if button is clicked, close the custom dialog
        return msg;
    }
}