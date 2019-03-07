package com.example.wyj.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class Utils {
    public static int winNum_5 = 5;

    public static boolean checkWin(int x, int y, List<Point> points, int winNum) {
        boolean isWin = checkHorizontal(x, y, points, winNum) || checkLeft(x, y, points, winNum) || checkRight(x, y, points, winNum) || checkVertical(x, y, points, winNum);
        return isWin;
    }

    public static boolean checkHorizontal(int x, int y, List<Point> points, int winNum) {
        int count = 1;

        for (int i = 1; i < winNum; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }

        if (count == winNum) {
            return true;
        }

        for (int i = 1; i < winNum; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }

        if (count == winNum) {
            return true;
        }

        return false;
    }

    public static boolean checkVertical(int x, int y, List<Point> points, int winNum) {
        int count = 1;

        for (int i = 1; i < winNum; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == winNum) {
            return true;
        }

        for (int i = 1; i < winNum; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == winNum) {
            return true;
        }

        return false;
    }

    public static boolean checkLeft(int x, int y, List<Point> points, int winNum) {
        int count = 1;

        for (int i = 1; i < winNum; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == winNum) {
            return true;
        }

        for (int i = 1; i < winNum; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == winNum) {
            return true;
        }

        return false;
    }

    public static boolean checkRight(int x, int y, List<Point> points, int winNum) {
        int count = 1;

        for (int i = 1; i < winNum; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == winNum) {
            return true;
        }

        for (int i = 1; i < winNum; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == winNum) {
            return true;
        }

        return false;
    }

    //模拟点击
    public static void myClickEvent(float x, float y, View view) {
        long firstTime = SystemClock.uptimeMillis();
        final MotionEvent firstEvent = MotionEvent.obtain(firstTime, firstTime, MotionEvent.ACTION_DOWN, x, y, 0);
        long secondTime = firstTime + 30;
        final MotionEvent secondEvent = MotionEvent.obtain(secondTime, secondTime, MotionEvent.ACTION_UP, x, y, 0);
        view.dispatchTouchEvent(firstEvent);
        view.dispatchTouchEvent(secondEvent);
    }

    public static void alert(String msg, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("重新开局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
