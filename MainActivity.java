


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.view.View;
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
    private ImageView star;


    //サイズ
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    //位置
    private float boxY;
    private float orangeX,orangeY;
    private float pinkX;
    private float pinkY;
    private float blackX;
    private float blackY;
    private float starX;
    private float starY;



    //スピード
    private int boxSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;
    private int starSpeed;

    //追加速度
    private int OrangeSpeedAdd = 0;
    private int PinkSpeedAdd = 0;
    private int BlackSpeedAdd = 0;



    //score
    private int score = 0;

    //Hnadler & Timer
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    //Status
    private boolean action_flg = false;
    private boolean start_flg =false;

    //Sound
    private SoundPlayer soundPlayer;

    private float moveSpeed = 50.0f;
    private float boxMoveSpeed =0;

    //スポーン間隔
    private int orangeSpawn = 20;
    private int blackSpawn = 10;
    private int pinkSpawn = 5000;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        black = findViewById(R.id.black);
        star = findViewById(R.id.star);




        //Screen Size
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;



        orange.setX(-80.0f);
        orange.setY(-80.0f);
        pink.setX(-80.0f);
        pink.setY(-80.0f);
        star.setX(-80.0f);
        star.setY(-80.0f);
        black.setX(-80.0f);
        black.setY(-80.0f);



        scoreLabel.setText("Score:0");





    }

    public void changePos(){

        boxSpeed = Math.round(screenHeight/(boxMoveSpeed+70));//四捨五入しint型に代入
        orangeSpeed = Math.round(screenWidth/(moveSpeed+35))- OrangeSpeedAdd;
        pinkSpeed = Math.round(screenWidth/(moveSpeed+11)) - PinkSpeedAdd;
        blackSpeed = Math.round(screenWidth/(moveSpeed+20)) - BlackSpeedAdd;

        starSpeed = Math.round(screenWidth/(90.0f));


        hitCheck();

        //Orange
        orangeX -= orangeSpeed;      //動く速度
        if(orangeX < 0){
            orangeX = screenWidth + orangeSpawn;         //スポーンX座標(枠外にスポーン)
            orangeY = (float)Math.floor(Math.random()*(frameHeight-orange.getHeight()));        //スポーンY座標(ランダムに生成)
        }

        orange.setX(orangeX);
        orange.setY(orangeY);

        //Black
        blackX -= blackSpeed;
        if(blackX < 0){
            blackX = screenWidth + blackSpawn;
            blackY = (float)Math.floor(Math.random()*(frameHeight-black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        //pink
        pinkX -= pinkSpeed;
        if(pinkX < 0){
            pinkX = screenWidth + pinkSpawn;
            pinkY = (float)Math.floor(Math.random()*(frameHeight-pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //star
        starX -= starSpeed;
        if(starX < 0){
            starX = screenWidth + (7500 * ((float)Math.floor(Math.random()*10)+1));; //スポーン位置調整　(7500*(0~10の乱数+1))
            starY = (float)Math.floor(Math.random()*(frameHeight-star.getHeight()));
        }
        star.setX(starX);
        star.setY(starY);


        //Box
        if(action_flg){         //action_figがtrueならboxYを上へ
            //Touching
            boxY -= boxSpeed;
        }else{
            //Releasing
            boxY += boxSpeed;         //action_figがfalseならboxYを下へ
        }

        if(boxY < 0) boxY = 0; //frameの外に出ていかないようにする

        if(boxY>frameHeight -boxSize)boxY = frameHeight- boxSize;   //同上  frame の高さからボックスの高さを引いた値

        box.setY(boxY);

        scoreLabel.setText("Score:" + score);


    }

    public void hitCheck(){
        //orange
        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        if(hitStatus(orangeCenterX,orangeCenterY)){

            orangeX = -10.0f;
            score += 10;
            soundPlayer.playHitSound();
        }

        //pink
        float pinkCenterX = pinkX + pink.getWidth() / 2;
        float pinkCenterY = pinkY + pink.getHeight() / 2;

        if(hitStatus(pinkCenterX,pinkCenterY)){

            pinkX = -10.0f;
            score += 30;
            soundPlayer.playHitSound();
        }


        //star
        float starCenterX = starX + star.getWidth() / 2;
        float starCenterY = starY + star.getHeight() / 2;

        if(hitStatus(starCenterX,starCenterY)){
            soundPlayer.playHitSound();
            starX = -10.0f;
            star();

            TimerTask task = new TimerTask() {
                public void run() {

                    orangeSpawn = 20;
                    blackSpawn = 10;
                    pinkSpawn = 5000;

                    //追加の増減速度を0に戻す
                    OrangeSpeedAdd = 0;
                    PinkSpeedAdd = 0;
                    BlackSpeedAdd = 0;


                }
            };

            Timer timer = new Timer();
            timer.schedule(task, 15000);


        }



        //black
        float blackCenterX = blackX + black.getWidth() /2;
        float blackCenterY = blackY + black.getHeight() /2;

        if(hitStatus(blackCenterX,blackCenterY)){

            soundPlayer.playOverSound();

            //GameOver!
            if(timer != null){
                timer.cancel();
                timer = null;
            }

            //結果画面へ
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);

        }



    }
    public void star(){
        //各スポーン間隔調整
        orangeSpawn = 1;
        pinkSpawn = 1;
        blackSpawn =  10000;

        blackX=blackSpawn;
        BlackSpeedAdd = blackSpeed;        //強制で速度を落とす

        OrangeSpeedAdd = 5 - (int)Math.floor(Math.random()*10);       //速度を+5~-5の乱数の値で増減
        PinkSpeedAdd = 5 - (int)Math.floor(Math.random()*10);
        //スコアが400以上であれば、上記の追加増減速度からさらに速度を減速させる
        if(score>=400){
            OrangeSpeedAdd += 5;
            PinkSpeedAdd += 10;
        }

        System.out.println("オレンジ：" + orangeSpeed);

    }



    public  boolean hitStatus(float centerX , float centerY){
        return(0 <= centerX && centerX <= boxSize  &&
                boxY <= centerY && centerY <= boxY + boxSize) ? true :false; //衝突した場合は true 衝突しなかった場合は falseを返す
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(start_flg ==  false){

            start_flg = true;

            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxY = box.getY();
            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {        //タイマータスク作成
                @Override
                public void run() {     //タイマータスクで実行する処理
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            },0,15);



        }else{
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                action_flg = true;      //画面に触れたらtrue

                //スコアによる速度調整
                if(100<=score && score<200){
                    moveSpeed=50.0f;
                }else if(200<=score && score<300){
                    moveSpeed=40.0f;
                }else if(300<=score && score<400){
                    boxMoveSpeed = -20.0f;
                    moveSpeed=30.0f;
                }else if(400<=score && score<600){
                    moveSpeed-=0.05f;
                }else if(600<=score && score<1000){
                    moveSpeed=10.0f;
                    moveSpeed-=0.1f;
                }else if(1000<=score && score<1500){
                    moveSpeed -=0.5f;
                }else if (1500<=score){
                    moveSpeed -=1.0f;
                }

                //各速度の参考に
                System.out.println("ムーブ：" + moveSpeed);
                System.out.println("オレンジ：" + orangeSpeed);
                System.out.println("ピンク" + pinkSpeed);
                System.out.println("ブラック" + blackSpeed);

            }else if(event.getAction()== MotionEvent.ACTION_UP){
                action_flg = false;     //画面から離したらfalse
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() { }
}
