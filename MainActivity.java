package com.example.catchtheballapk;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView orange;
    private ImageView pink;
    private ImageView black;

    ///size
    private  int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;


    ///position
    private int boxY;
    private int orangeX;
    private int orangeY;
    private int pinkX;
    private int pinkY;
    private int blackX;
    private int blackY;

    //score
    private int score =0;

    ///initialization class
    private Handler handler=new Handler();
    private Timer timer= new Timer();
    private Sound sound;

    ///status check
    private boolean action_flg=false;
    private boolean start_flg=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new Sound(this);
        scoreLabel=(TextView) findViewById(R.id.scoreLabel);
        startLabel=(TextView) findViewById(R.id.startLabel);
        box=(ImageView) findViewById(R.id.box);
        orange=(ImageView) findViewById(R.id.orange);
        pink=(ImageView) findViewById(R.id.pink);
        black=(ImageView) findViewById(R.id.black);

    /// get screen size
        WindowManager wm=getWindowManager();
        Display disp= wm.getDefaultDisplay();
        Point size =new Point();
        disp.getSize(size);

        screenWidth=size.x;
        screenHeight=size.y;



     ///   move out of screen
        orange.setX(-100);
        orange.setY(-100);
        pink.setX(-90);
        pink.setY(-90);
        black.setX(-90);
        black.setY(-90);
        scoreLabel.setText("Score : 0") ;


    }

    public void changePos(){

        hitcheak();


        /// orange
        orangeX-=17;
        if(orangeX<0){
            orangeX = screenWidth + 20;
            orangeY=(int)Math.floor(Math.random()*(frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        ///black
        blackX-=19;
        if (blackX<0){
            blackX= screenWidth + 10;
            blackY= (int)Math.floor(Math.random()*(frameHeight - orange.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);


        ///pink
        pinkX-=22;
        if(pinkX<0){
           pinkX= screenWidth +5500;
            pinkY=(int)Math.floor(Math.random()*(frameHeight - orange.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        ///move box
        if (action_flg==true) {
            ///touching
            boxY -= 25;
        }
        else{
            //realizing
            boxY+=25;
        }

     ///check box position.
     if(boxY<0)boxY=0;

     if(boxY>frameHeight-boxSize)boxY=frameHeight-boxSize;



        box.setY(boxY);
        scoreLabel.setText("Score : "+score) ;

    }

    public void hitcheak(){
        // if center of the ball is in the box it counts hits

        //orange
        int orangeCenterX = orangeX+orange.getWidth()/2;
        int orangeCenterY = orangeY+orange.getHeight()/2;

        //0<=orangeCenterX<=boxWidth
        //boxY<= orangeCenterY<=boxY+boxHeight

        if(0 <= orangeCenterX && orangeCenterX <= boxSize &&
        boxY <= orangeCenterY && orangeCenterY <= boxY+boxSize){
           score +=10;
           orangeX=-10;
           sound.playHitSound();
        }

        //pink
        int pinkCenterX = pinkX + pink.getWidth()/2;
        int pinkCenterY = pinkY + pink.getHeight()/2;

        if(0 <= pinkCenterX && pinkCenterX <= boxSize &&
                boxY <= pinkCenterY && pinkCenterY <= boxY+boxSize){
                score +=30;
                pinkX=-10;
                sound.playHitSound();
        }

        //black
        int blackCenterX = blackX + black.getWidth()/2;
        int blackCenterY = blackY + black.getHeight()/2;

        if(0 <= blackCenterX && blackCenterX <= boxSize &&
                boxY <= blackCenterY && blackCenterY <= boxY+boxSize){

            //stop timer !!
            timer.cancel();
            timer=null;

            sound.playOverSound();

            //show result
            Intent intent =new Intent(getApplicationContext(),result.class);
            intent.putExtra("Score",score);
            startActivity(intent);

        }
    }


        //touch screen
    public boolean onTouchEvent(MotionEvent me){
        if (start_flg == false){

            start_flg=true;
         ///why get frame height and box height here?
         ///because the ui has not been set on the screen in OnCreate()!!

            FrameLayout frame= (FrameLayout)findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxY=(int) box.getY();

         ///the box is a square.(height and weight are the same.)
            boxSize=box.getHeight();



            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask(){
                public void run(){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            },0,20);

        }else{
            if (me.getAction()==MotionEvent.ACTION_DOWN) {
                action_flg=true;
            }else if (me.getAction()==MotionEvent.ACTION_UP){
                action_flg=false;
            }
        }


            return  true;
    }

    //disable return button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getAction()==KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

}