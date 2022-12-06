package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

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
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    final MaterialButton buttons[][] = new MaterialButton[9][9];
    final int[] buttonID = new int[]{R.id.num1, R.id.num2, R.id.num3,
            R.id.num4, R.id.num5, R.id.num6, R.id.num7,
            R.id.num8, R.id.num9, R.id.cancel, R.id.confirm};
    final Context context = this;
    final int[][] groups = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    int[] margin = new int[4];
    int selectedNum = -1;
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
                        initGame();
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
        board = new BoardGenerator();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectedNum = -1;
                // 보드 위 버튼들에 대한 ID를 직접 부여
                arg0.setId(ViewCompat.generateViewId());

                int button_id = arg0.getId();
                View dialogButton = null;

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.number_pad);
                TextView topNum = (TextView) dialog.findViewById(R.id.selectedNum);

                // 숫자패드 내에 있는 모든 버튼에 onClick 메소드를 추가한다.
                for (int i = 0; i < buttonID.length; i++) {
                    if (i < 9) {
                        // 숫자 버튼
                        dialogButton = (Button) dialog.findViewById(buttonID[i]);
                    } else {
                        // 취소, 확인 버튼
                        dialogButton = (ImageButton) dialog.findViewById(buttonID[i]);
                    }
                    int finalI = i;

                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 클릭된 버튼 값
                            String num = String.valueOf(finalI + 1);

                            // 클릭된 버튼이 숫자 버튼일 경우, 값을 상단에 표시한다.
                            if (finalI < 9) {
                                topNum.setText(num);
                                selectedNum = finalI + 1;
                                Toasty.info(context, initMessage(finalI)).show();
                            }
                            // 취소버튼일 경우 아무런 동작도 하지 않고 다이얼로그를 없앤다.
                            if(finalI == 9) {
                                dialog.dismiss();
                                Toasty.info(context, initMessage(finalI)).show();
                            }
                            // 확인 버튼일 경우, 숫자가 선택된 상태라면 보드의 블록 값을 변경한다.
                            if(finalI == 10) {
                                if(selectedNum > -1)
                                    changeButtonNum(button_id, Integer.toString(selectedNum));
                                dialog.dismiss();
                                Toasty.info(context, initMessage(finalI)).show();
                            }
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
                buttons[row][col].setEnabled(false);
            }
            tableLayout.addView(tableRow);
        }
        createEmptyButton();
    }

    @SuppressLint("ResourceAsColor")
    private void changeButtonNum(int button_id, String num) {
        Button btn = (Button) findViewById(button_id);
        int[] index = findBtnIndex(btn);
        // 버튼 숫자 변경
        if (!num.isEmpty() && btn != null) {
            btn.setText(num);
        }
        // 버튼 숫자의 유효성 검사
        SudokuRule rule = new SudokuRule(extractBoard());

        if (rule.check(index[0], index[1])) {
            btn.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            btn.setTextColor(getResources().getColor(R.color.light_blue_A400));
        } else {
            btn.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.salmon)));
            btn.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private int[] findBtnIndex(Button btn) {
        int[] index = new int[2];
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                if (buttons[i][j].equals(btn)) {
                    index[0] = i;
                    index[1] = j;
                    return index;
                }
            }
        }
        return index;
    }

    private int[][] extractBoard() {
        int[][] board = new int[9][9];
        String txt = "";
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                Button btn = buttons[i][j];
                txt = (String) btn.getText();
                if (txt.isEmpty())
                    board[i][j] = 0;
                else
                    board[i][j] = Integer.parseInt(txt);
            }
        }
        return board;
    }
    /* [빈 블록 생성 과정]
    1. 랜덤하게 선정된 n개의 빈 블록 좌표를 받는다.
    2. 해당 좌표의 버튼을 받아온다.
    3. 해당 버튼 위에 빈 버튼을 겹치게 생성한다. -> 아마 레이아웃이 필요할듯?

    ** 베이스 보드에 있는 버튼들은 클릭 비허용으로 바꿔야함!! **

    [빈 블록 속성]
    1. 클릭 허용 -> 온클릭 리스너로 다이어그램 띄우기
    2. 다이어그램에서 선택된 숫자를 받아서 블록의 text로 설정하기
    하 숫자 여러 개 넣는 건 또 어떻게 해야돼 개어렵네
    */

    private void createEmptyButton() {
        SudokuRule rule = new SudokuRule(board.get());
        ArrayList<int[]> emptyButtons = rule.setAllEmptyButton();
        int row, col;
        for (int[] btns : emptyButtons) {
            row = btns[0];
            col = btns[1];
            buttons[row][col].setText("");
            buttons[row][col].setEnabled(true);
        }
    }

    // 게임을 초기화하는 함수.
    private void initGame() {
        BoardGenerator board = new BoardGenerator();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String number = Integer.toString(board.get(i, j));
                buttons[i][j].setText(number);
                buttons[i][j].setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                buttons[i][j].setTextColor(getResources().getColor(R.color.black));
            }
        }
        createEmptyButton();
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
        buttons[i][j].setTextColor(getResources().getColor(R.color.black));
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