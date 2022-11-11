package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku);
        initContent();
    }

    private void initContent() {
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        // 버튼이 화면을 넘어가지 않도록 자동 크기 조절
        tableLayout.setShrinkAllColumns(true);

        Button buttons[][] = new Button[9][9];
        for (int i = 0; i < 9; i++) {
            final TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
            );
            for (int j = 0; j < 9; j++) {

                BoardGenerator board = new BoardGenerator();
                buttons[i][j] = new Button(this);
                buttons[i][j].setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT)
                );
                // board.get(i, j)는 int를 반환하고 setText는 String을 받기 때문에 String으로 형변환 필요
                String number = Integer.toString(board.get(i, j));
                buttons[i][j].setText(number);
                tableRow.addView(buttons[i][j]);
            }
            tableLayout.addView(tableRow);
        }
    }
}