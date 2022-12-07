package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        initContent();
    }

    private void initContent() {
        // 버튼이 화면을 넘어가지 않도록 자동 크기 조절
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        tableLayout.setShrinkAllColumns(true);
        initResetButton();
        initSubmitButton();
        createButton(tableLayout);
    }

    private void createButton(TableLayout tableLayout) {
        board = new BoardGenerator();
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "nanum_r.otf");
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectedNum = -1;
                // 보드 위 버튼들에 대한 ID를 직접 부여
                arg0.setId(ViewCompat.generateViewId());

                int button_id = arg0.getId();
                View dialogButton = null;

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_numpad);
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
                            if (finalI == 9) {
                                dialog.dismiss();
                                Toasty.info(context, initMessage(finalI)).show();
                            }
                            // 확인 버튼일 경우, 숫자가 선택된 상태라면 보드의 블록 값을 변경한다.
                            if (finalI == 10) {
                                if (selectedNum > -1)
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
            tableRow.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT)
            );
            tableRow.setPadding(0, 0, 8, -13);
            for (int col = 0; col < 9; col++) {
                buttons[row][col] = new MaterialButton(context);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f);

                // 중첩 순서 : tablerow -> framelayout -> dialog -> button
                // 1. FrameLayout 생성
                FrameLayout frameLayout = new FrameLayout(context);
                tableRow.addView(frameLayout);
                setPadding(row, col);
                frameLayout.setPadding(padding[0], padding[1], padding[2], padding[3]);

                //2. Memo Pad 생성
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View memo = layoutInflater.inflate(R.layout.layout_memo, frameLayout, true);
                memoLayouts[row][col] = memo;
                // 3. Button 생성
                String number = Integer.toString(board.get(row, col));
                setButtonDesign(row, col, number, layoutParams, listener);
                buttons[row][col].setEnabled(false);
                buttons[row][col].setTypeface(typeFace);
                frameLayout.addView(buttons[row][col]);

                // 4. Button map에 두 인스턴스를 추가
                memoMap.put(buttons[row][col], memoLayouts[row][col]);
            }
            tableLayout.addView(tableRow);
        }
        createEmptyButton();
    }

    private void initResetButton() {
        final Button button = (Button) findViewById(R.id.resetButton);
        final String[] resetMessage = {"게임이 초기화되었습니다.", "초기화 취소"};
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_reset);

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

    private void initSubmitButton() {
        final Button button = (Button) findViewById(R.id.submitButton);
        final String[] submitiMessage = {"클리어되지 않았습니다.", "게임이 클리어되었습니다.", "게임을 종료합니다.", "게임을 재시작합니다."};
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 1. 게임 클리어 여부 검사
                // 2. 클리어되지 않았다면 단순한 토스트 띄우기
                if(!checkGameClear()) {
                    Toasty.info(context, submitiMessage[0]).show();
                    return;
                }

                // 3. 클리어 시 다이얼로그 생성
                Toasty.info(context, submitiMessage[1]).show();
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_win);

                Button reset_exit = (Button) dialog.findViewById(R.id.exit_button);
                Button reset_replay = (Button) dialog.findViewById(R.id.replay_button);

                // 1) 게임 나가기 버튼
                reset_exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        Toasty.info(context, submitiMessage[2]).show();
                    }
                });
                // 2) 게임 재시작 버튼
                reset_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initGame();
                        dialog.dismiss();
                        Toasty.info(context, submitiMessage[3]).show();
                    }
                });
                dialog.show();
            }
        });
    }

    private boolean checkGameClear() {
        return true;
    }

    private void createEmptyButton() {
        SudokuRule rule = new SudokuRule(board.get());
        ArrayList<int[]> emptyButtons = rule.setAllEmptyButton();
        int row, col;
        for (int[] btns : emptyButtons) {
            row = btns[0];
            col = btns[1];
            buttons[row][col].setText("");
            buttons[row][col].setEnabled(true);
            View memo = memoMap.get(buttons[row][col]);

            // 메모 기능 추가를 위한 OnLongClickListener 추가
            buttons[row][col].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setId(ViewCompat.generateViewId());
                    int button_id = v.getId();

                    Button btn = (Button) findViewById(button_id);
                    btn.setVisibility(View.INVISIBLE);

                    selectedMemo = -1;

                    View dialogButton = null;
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_memo);
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
                                    selectedMemo = finalI + 1;
                                    Toasty.info(context, initMessage(finalI)).show();
                                }
                                // 취소버튼일 경우 아무런 동작도 하지 않고 다이얼로그를 없앤다.
                                if (finalI == 9) {
                                    dialog.dismiss();
                                    Toasty.info(context, initMessage(finalI)).show();
                                }
                                // 확인 버튼일 경우, 숫자가 선택된 상태라면 보드의 블록 값을 변경한다.
                                if (finalI == 10) {
                                    View memo = null;
                                    for (MaterialButton e : memoMap.keySet()) {
                                        // 클릭된 아이디에 매핑되는 메모패드를 가져온다.
                                        if (e.getId() == btn.getId()) {
                                            memo = memoMap.get(e);
                                        }
                                    }
                                    if (selectedMemo > -1)
                                        changeMemoNum(memo, selectedMemo);
                                    dialog.dismiss();
                                    Toasty.info(context, initMessage(finalI)).show();
                                }
                            }
                        });
                    }
                    dialog.show();
                    return false;
                }
            });

            memo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) { //view == 메모 레이아웃
                    v.setId(ViewCompat.generateViewId());
                    int memoId = v.getId();
                    View memoLayout = (View) findViewById(memoId);
                    selectedMemo = -1;

                    View dialogButton = null;
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_memo);
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
                                    selectedMemo = finalI + 1;
                                    Toasty.info(context, initMessage(finalI)).show();
                                }
                                // 취소버튼일 경우 아무런 동작도 하지 않고 다이얼로그를 없앤다.
                                if (finalI == 9) {
                                    dialog.dismiss();
                                    Toasty.info(context, initMessage(finalI)).show();
                                }
                                // 확인 버튼일 경우, 숫자가 선택된 상태라면 보드의 블록 값을 변경한다.
                                if (finalI == 10) {
                                    if (selectedMemo > -1)
                                        changeMemoNum(memoLayout, selectedMemo);
                                    dialog.dismiss();
                                    Toasty.info(context, initMessage(finalI)).show();
                                }
                            }
                        });
                    }
                    dialog.show();
                    return false;
                }
            });

            // 메모패드를 짧게 클릭할 경우 원래 버튼으로 되돌아감
            memo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setId(ViewCompat.generateViewId());
                    int memoId = v.getId();
                    View memoLayout = (View) findViewById(memoId);

                    for (MaterialButton b : memoMap.keySet()){
                        View m = memoMap.get(b);
                        if(memoLayout.getId() == m.getId()) {
                            b.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            });
        }
    }

    private void changeMemoNum(View memo, int selectedMemo) {
        if (memo != null) {
            TextView memoNum = memo.findViewById(memoID[selectedMemo - 1]);
            System.out.println("**** 클릭된 버튼 : " + memoNum.getText());

            int color = memoNum.getCurrentTextColor();
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            System.out.println("*** memoNum.getText() 버튼의 현재 색상: " + hexColor + "\n");

            if (hexColor.equals("#000000")) //이미 클릭된 적 있다면, 취소를 의미하는 흰색으로 바꿈
                memoNum.setTextColor(getResources().getColor(R.color.white));
            else memoNum.setTextColor(getResources().getColor(R.color.black));
        }
    }

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

    private void initGame() {
        BoardGenerator board = new BoardGenerator();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String number = Integer.toString(board.get(i, j));
                buttons[i][j].setText(number);
                buttons[i][j].setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                buttons[i][j].setTextColor(getResources().getColor(R.color.black));
                buttons[i][j].setEnabled(false);
            }
        }
        createEmptyButton();
        for (View memo : memoMap.values()) {
            for(int id : memoID){
                TextView memoNum = memo.findViewById(id);
                int color = memoNum.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                if (hexColor.equals("#000000"))
                    memoNum.setTextColor(getResources().getColor(R.color.white));
            }
        }
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

    private void initPadding() {
        for (int i = 0; i < padding.length; i++)
            padding[i] = 1;
    }

    private void setPadding(int i, int j) {
        // [0] = left, [1] = top, [2] = right, [3] = bottom)
        initPadding();
        if (j == 8) padding[2] = 8;
        if (j == 0) padding[0] = 8;
        if (i == 8) padding[3] = 8;
        if (i == 0) padding[1] = 8;
        if (j == 3 || j == 6) padding[0] = 4;
        if (i == 3 || i == 6) padding[1] = 4;
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

    final MaterialButton buttons[][] = new MaterialButton[9][9];
    final int[] buttonID = new int[]{R.id.num1, R.id.num2, R.id.num3,
            R.id.num4, R.id.num5, R.id.num6, R.id.num7,
            R.id.num8, R.id.num9, R.id.cancel, R.id.confirm};
    final int[] memoID = new int[]{R.id.memo1, R.id.memo2, R.id.memo3,
            R.id.memo4, R.id.memo5, R.id.memo6, R.id.memo7, R.id.memo8, R.id.memo9};
    View[][] memoLayouts = new View[9][9];

    HashMap<MaterialButton, View> memoMap = new HashMap<>();

    final Context context = this;
    final int[][] groups = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    int[] padding = new int[4];
    int selectedNum = -1;
    int selectedMemo = -1;
    BoardGenerator board;
}