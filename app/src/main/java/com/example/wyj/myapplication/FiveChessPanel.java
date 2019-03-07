package com.example.wyj.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class FiveChessPanel extends View {
    //宽
    private int maxPanelWidth;
    //每个方格高度（正方形，宽高一样）
    private float maxLineHeight;
    //一行多少条线
    private int MAX_LINE = 15;
    //5个棋连成一条线为胜利
    private int MAX_COUNT_IN_LINE = 5;
    //画笔
    private Paint mPaint = new Paint();
    //棋盘上的白棋黑棋（宽高）
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    //棋子占方格的高度比
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    //白棋先手，或者当前为白棋
    private boolean mIsWhite = true;
    //白棋黑棋数组
    private List<Point> mWhiteArray = new ArrayList<>();
    private List<Point> mBlackArray = new ArrayList<>();
    //判断游戏是否结束
    private boolean mIsGameOver;
    //判断哪种棋获胜
    private boolean mIsWhiteWinner;

    //音效
    private SoundPool soundWin = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
    private SoundPool soundChess = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);


    //构造函数
    public FiveChessPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    //初始化棋盘，棋子图片资源
    private void init() {
        mPaint.setColor(Color.BLACK);
        //抗锯齿，抗抖动，参考链接https://blog.csdn.net/lovexieyuan520/article/details/50732023
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        //线条样式
        mPaint.setStyle(Paint.Style.STROKE);
        //获取图片资源
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        //获取音效资源
        soundWin.load(getContext(), R.raw.win, 1);
        soundChess.load(getContext(), R.raw.chess, 1);
    }


    //测量本自定义的View需要多大的空间（参考父布局的大小做出改动）
    //参考链接https://blog.csdn.net/xmxkf/article/details/51490283
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取父布局要求的测量模式与测量尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //为了能正常显示方格，取最小的宽度来布局成正方形
        int width = Math.min(widthSize, heightSize);
        //如果父组件无约束（防止出现view显示不出来的情况，特别是嵌套其他布局的情况，或者旋转屏幕）
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        //设置布局
        setMeasuredDimension(width, width);
    }


    //尺寸变化后调用的监听函数
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //当前宽度
        maxPanelWidth = w;
        //设置每个方格的高度(但是，如果15条线，只有14个格子，要考虑棋子下在边缘时不能溢出，否则会布局导致显示不全)
        maxLineHeight = maxPanelWidth * 1.0f / MAX_LINE;
        //棋子宽度，占每个方格的3/4，也就是占两个格子的3/8
        int pieceWidth = (int) (maxLineHeight * ratioPieceOfLineHeight);
        //从当前存在的棋子位图（图片大小），按一定的比例创建一个新的棋子位图（布局上显示，正方形，长宽一样）
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }


    //绘制棋盘，棋子
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPiece(canvas);

        checkGameOver();
    }


    //绘制棋盘
    private void drawBoard(Canvas canvas) {
        //拿到宽度
        int w = maxPanelWidth;
        //拿到高度
        float lineHeight = maxLineHeight;
        //根据MAX_LINE绘制棋盘，不是从布局的（0,0）开始；是从(lineHeight/2,lineHeight/2)开始
        //每行每列结束时也是减了lineHeight / 2
        for (int i = 0; i < MAX_LINE; i++) {
            int startx = (int) (lineHeight / 2);
            int endx = (int) (w - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            //画线
            canvas.drawLine(startx, y, endx, y, mPaint);
            canvas.drawLine(y, startx, y, endx, mPaint);
        }
    }


    //检测游戏谁赢，游戏是否结束
    private void checkGameOver() {
        //判断黑棋白棋是否有5个
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        //判断谁赢
        if (whiteWin || blackWin) {
            //无论谁赢，设为true
            mIsGameOver = true;
            //判断黑棋还是白棋赢了
            mIsWhiteWinner = whiteWin;
            String text = mIsWhiteWinner ? "黑旗胜利" : "白棋胜利";

            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
        }
    }


    //检测每个棋子的横竖，斜上斜下
    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            //把判断的函数放到工具类里，减少代码复杂度
            boolean win = Utils.checkWin(x,y,points,Utils.winNum_5);
            if(win){
                soundWin.play(1, 1, 1, 0, 0, 1);
                Utils.alert("您赢了！",this);
                return true;
            }
        }
        return false;
    }


    //绘制棋子

    private void drawPiece(Canvas canvas) {
        //根据白棋黑棋的数组来绘制
        for (int i = 0; i < mWhiteArray.size(); i++) {
            Point whitePoint = mWhiteArray.get(i);
            //绘制棋子在棋盘的位置，要考虑绘制棋子起始的坐标是(0,0)，而棋盘绘制的起始坐标是(lineHeight/2,lineHeight/2)
            //然后，再当前坐标乘以方格长度
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * maxLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * maxLineHeight,
                    null);

        }
        for (int i = 0; i < mBlackArray.size(); i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * maxLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * maxLineHeight,
                    null);

        }
    }


    //点击事件的监听函数

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //每次点击都要判断游戏是否结束
        if (mIsGameOver) return false;
        int action = event.getAction();
        //考虑用户的点击操作，例如ACTION_DOWN，用户可能点击后，可能还在思考该不该下，
        //就像在生活中下棋，棋子落入棋盘时，
        //人们一般会拿中指抬住棋子的末端，这时棋子并不是全部放在棋盘上，是还可以移动的，
        //所以，应该监听的是MotionEvent中ACTION_UP事件：最后一个触摸点消失时产生的事件
        //可以理解为点击后，最后一个手指抬起时产生的事件（点击后可以移动）
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            //检测棋子，也就是手指点击抬起后，是否在两条线交叉的点附近，给强制性转为在交叉点上的点
            Point p = getValidPoint(x, y);
            //判断是否重复点击（ArrayList中contains比较的是数值，不是内存单元，所以可以直接用contains方法）
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }
            //重复点同一个地方不能播放音乐
            soundChess.play(1, 1, 1, 0, 0, 1);
            //判断时白旗还是黑棋
            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            //强制重绘
            invalidate();
            //白棋，黑棋轮换
            mIsWhite = !mIsWhite;
            return true;
        }
        return true;
    }
    //借用将浮点数取整，来固定棋子为整数的坐标

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / maxLineHeight), (int) (y / maxLineHeight));
    }
}
