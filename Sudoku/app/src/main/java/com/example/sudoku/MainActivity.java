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
        setContentView(R.layout.activity_main);

        // 1. Table Layout 생성
        TableLayout table = (TableLayout)findViewById(R.id.tableLayout);
        TableRow tableRow = new TableRow(this);
        table.addView(tableRow);

        // 2. Button 생성
        Button[][] buttons = new Button[9][9];
        int left= 1;
        int top = 1;
        int right= 1;
        int bottom = 1;

        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                buttons[i][j] = new Button(this);
                tableRow.addView(buttons[i][j]);
                TableRow.LayoutParams layoutParams =
                        new TableRow.LayoutParams(
                                TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT,
                                1.0f
                        );
                layoutParams.setMargins(left, top, right, bottom);
                buttons[i][j].setLayoutParams(layoutParams);
                tableRow.addView(buttons[i][j]);
                // 3. Button 위의 숫자 생성
                BoardGenerator board = new BoardGenerator();
                int number = board.get(i, j);
                buttons[i][j].setText(number);
            }
        }

    }
}