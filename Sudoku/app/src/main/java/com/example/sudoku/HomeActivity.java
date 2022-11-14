package com.example.sudoku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    int[] margin = new int[4];
    final Button buttons[][] = new Button[9][9];
    final int[] buttonID = new int[]{R.id.num1, R.id.num2, R.id.num3,
            R.id.num4, R.id.num5, R.id.num6, R.id.num7, 
            R.id.num8, R.id.num9, R.id.cancel, R.id.confirm};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initContent();
    }

    private void createButton(TableLayout tableLayout, Context context) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                String msg = null;
                dialog.setContentView(R.layout.custom);
                for (int i = 0; i < buttonID.length; i++) {
                    Button dialogButton = (Button) dialog.findViewById(buttonID[i]);
                    if(i < 9) 
                        msg = (i + 1) + "번";
                    if(buttonID[i] == R.id.cancel)
                        msg = "취소";
                    if(buttonID[i] == R.id.confirm)
                        msg = "확인";
                    msg += " 버튼이 클릭되었습니다.";
                    // if button is clicked, close the custom dialog
                    String finalMsg = msg;
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), finalMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                dialog.show();
            }
        };

        // i=row, j=column을 의미함
        for (int i = 0; i < 9; i++) {
            final TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
            );
            for (int j = 0; j < 9; j++) {
                initMargin();
                BoardGenerator board = new BoardGenerator();
                buttons[i][j] = new Button(this);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f);
                // 3개 단위로 margin을 크게 주는 함수
                setMargin(i, j);
                // setMargins(left, top, right, bottom)
                layoutParams.setMargins(margin[0], margin[1], margin[2], margin[3]);
                buttons[i][j].setLayoutParams(layoutParams);
                // board.get(i, j)는 int를 반환하고 setText는 String을 받기 때문에 String으로 형변환 필요
                String number = Integer.toString(board.get(i, j));
                buttons[i][j].setText(number);
                tableRow.addView(buttons[i][j]);
                buttons[i][j].setOnClickListener(listener);
            }
            tableLayout.addView(tableRow);
        }
    }

    private void initContent() {
        // 버튼이 화면을 넘어가지 않도록 자동 크기 조절
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        tableLayout.setShrinkAllColumns(true);
        createButton(tableLayout, this);

        final Button button = (Button) findViewById(R.id.resetButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetNumber();
            }
        });
    }

    private void resetNumber() {
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
            margin[i] = 0;
    }

    private void setMargin(int i, int j) {
        if (j == 8) margin[2] = 20;
        if (j == 0) margin[0] = 20;
        if (i == 8) margin[3] = 20;
        if (i == 0) margin[1] = 20;
        if (j == 3 || j == 6) margin[0] = 10;
        if (i == 3 || i == 6) margin[1] = 10;
    }
}