package com.junhee.android.handmemo_ver03;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RadioGroup color_pick, background_color;
    FrameLayout memoLayout, backLayout;
    SeekBar opacity_back, opacity_stroke, stroke_width;
    Board board;
    BackGround backGround;
    ImageView snap_img;

    int opt_brush_color = Color.BLACK;
    float opt_brush_width = 10f;

    Bitmap captured = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memoLayout = (FrameLayout) findViewById(R.id.writeLayout);
        backLayout = (FrameLayout) findViewById(R.id.backLayout);
        snap_img = (ImageView) findViewById(R.id.snap_img);
        background_color = (RadioGroup) findViewById(R.id.backGround_color);

        opacity_stroke = (SeekBar) findViewById(R.id.stroke_opa);
        opacity_stroke.setProgress(10);
        opacity_stroke.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float float_progress = (float) progress * 0.01f;


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        opacity_back = (SeekBar) findViewById(R.id.backGround_opa);
        opacity_back.setProgress(1);
        opacity_back.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float float_progress = (float) progress * 0.01f;
                backGround.setAlpha(float_progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 스트로크 색상선택
        color_pick = (RadioGroup) findViewById(R.id.color_pick);
        color_pick.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.btnBlack:
                        setBrushColor(Color.BLACK);
                        break;

                    case R.id.btnBlue:
                        setBrushColor(Color.BLUE);
                        break;

                    case R.id.btnGreen:
                        setBrushColor(Color.GREEN);
                        break;

                    case R.id.btnRed:
                        setBrushColor(Color.RED);
                        break;
                }
            }
        });
        // 두께 선택
        stroke_width = (SeekBar) findViewById(R.id.stroke_width);
        stroke_width.setProgress(10);
        stroke_width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                opt_brush_width = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // 터치가 종료되었을 때만 값을 세팅해준다.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setBrushStroke(opt_brush_width);
            }
        });
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoLayout.destroyDrawingCache();
                memoLayout.buildDrawingCache();
                captured = memoLayout.getDrawingCache();
                snap_img.setImageBitmap(captured);

            }
        });
        // 백그라운드 색 선택
        background_color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.btnBgBlack:
                        backGround.setBackgroundColor(Color.BLACK);
                        break;

                    case R.id.btnBgBlue:
                        backGround.setBackgroundColor(Color.BLUE);
                        break;

                    case R.id.btnBgGreen:
                        backGround.setBackgroundColor(Color.GREEN);
                        break;

                    case R.id.btnBgRed:
                        backGround.setBackgroundColor(Color.RED);
                        break;

                    default:
                        backGround.setBackgroundColor(Color.WHITE);
                }
            }
        });
        board = new Board(getBaseContext());
        backGround = new BackGround(getBaseContext());
        memoLayout.addView(board);
        backLayout.addView(backGround);
        setBrush();
    }

    private void setBrushColor(int colorType) {
        opt_brush_color = colorType;
        setBrush();
    }

    private void setBrushStroke(float width) {
        opt_brush_width = width;
        setBrush();
    }

    // 현재 설정된 옵션값을 사용하여 브러쉬를 새로 생성하고 그림판에 담는다.
    private void setBrush() {
        Brush brush = new Brush();
        brush.color = opt_brush_color;
        brush.stroke = opt_brush_width;
        board.setBrush(brush);
    }


    class BackGround extends View {

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            invalidate();
        }

        public BackGround(Context context) {
            super(context);
        }

        @Override
        public void setAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
            super.setAlpha(alpha);
        }

        @Override
        public void setBackgroundColor(@ColorInt int color) {
            super.setBackgroundColor(color);
        }

    }

    class Board extends View {
        Paint paint;
        List<Brush> brushes;
        Brush current_brush;
        Path current_path;

        boolean newBrush = true;

        @Override
        public void setAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
            super.setAlpha(alpha);
        }

        @Override
        public void setBackgroundColor(@ColorInt int color) {
            super.setBackgroundColor(color);
        }

        public Board(Context context) {
            super(context);
            setPaint();
            brushes = new ArrayList<>();
        }

        // 처음 한번만 기본 페인트 속성을 설정해둔다.
        private void setPaint() {
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setDither(true);
        }

        // 브러쉬를 새로 생성한다.
        public void setBrush(Brush brush) {
            current_brush = brush;
            newBrush = true;
        }

        // Path를 새로 생성한다.
        private void createPath() {
            if (newBrush) {
                current_path = new Path();
                newBrush = false;
                current_brush.addPath(current_path);
                brushes.add(current_brush);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (Brush brush : brushes) {
                paint.setStrokeWidth(brush.stroke);
                paint.setColor(brush.color);
                canvas.drawPath(brush.path, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // 내가 터치한 좌표를 꺼낸다
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("ACTION_MOVE", "=======================[ACTION_MOVE");
                    createPath();
                    current_path.moveTo(x, y);
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.i("ACTION_MOVE", "=======================[ACTION_MOVE");
                    current_path.lineTo(x, y);
                    break;

                case MotionEvent.ACTION_UP:
                    break;
            }

            // 화면을 갱신해서 위에서 그린 Path를 반영해 준다.
            invalidate();

            // 리턴 false 일 경우 touch 이벤트를 연속해서 발생시키지 않는다.
            // 즉, 드래그시 onTouchEvent 가 호출되지 않는다
            return true;
        }
    }

    class Brush {
        Path path;      // 브러쉬로 그리는 경로를 같이 담아둔다
        int color;
        float stroke;

        public void addPath(Path path) {
            this.path = path;
        }
    }
}
