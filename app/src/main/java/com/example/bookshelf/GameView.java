package com.example.bookshelf;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameView extends View {
    private Paint ballPaint;
    private int x, y, radius = 100;
    private int score = 0;
    private Random random = new Random();

    public GameView(Context context) {
        super(context);
        ballPaint = new Paint();
        ballPaint.setColor(Color.RED);
        ballPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Ak je to prvé spustenie, nastav loptičku do stredu
        if (x == 0) {
            x = getWidth() / 2;
            y = getHeight() / 2;
        }

        // Vykreslenie skóre
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        canvas.drawText("Skóre: " + score, 50, 100, textPaint);

        // Vykreslenie loptičky
        canvas.drawCircle(x, y, radius, ballPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            // Kontrola, či sa hráč dotkol loptičky (Pytagorova veta)
            double distance = Math.sqrt(Math.pow(touchX - x, 2) + Math.pow(touchY - y, 2));

            if (distance <= radius) {
                score++;
                moveBall();
                invalidate(); // Prekresli obrazovku
            }
        }
        return true;
    }

    private void moveBall() {
        // Presunie loptičku na náhodné miesto (v rámci obrazovky)
        x = random.nextInt(getWidth() - radius * 2) + radius;
        y = random.nextInt(getHeight() - radius * 2) + radius;
    }
}