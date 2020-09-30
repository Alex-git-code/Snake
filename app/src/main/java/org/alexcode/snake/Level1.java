package org.alexcode.snake;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Level1 extends AppCompatActivity {
    Canvas canvas;
    PlayGroundView playGroundView;
    Bitmap headBitmap, bodyBitmap, foodBitmap, field, green, leftUpTree, leftDownTree, rightUpTree, rightDownTree, upSide, downSide, leftSide, rightSide;
    //for snake movement
    int directionOfTravel = 0; //0 = up, 1 = right, 2 = down, 3 = left
    //for control fps
    long lastFrameTime;
    int fps, gameSpeed = 150;
    //for the snake
    int [] snakeX, snakeY;
    int snakeLength;
    // for the food
    int foodX, foodY;
    //for display settings and draw
    int blockSize, numBlocksWide, numBlocksHigh, screenWidth, screenHeight;
    //score
    int score = 0, hiScore = 0;
    int playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureDisplay();
        playGroundView = new PlayGroundView(this);
        setContentView(playGroundView);
        playerId =  PlayerPreferences.getPlayerId();
        updatePlayerHiScore();
    }

    class PlayGroundView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSnake;
        Paint paint;

        public PlayGroundView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            //set snake max length
            snakeX = new int[10000];
            snakeY = new int[10000];
            //generate snake
            getSnake();
            //generate food
            getFood();
        }

        public void getSnake() {
            //set start snake length
            snakeLength = 3;
            //set start snake head (in the middle of screen)
            snakeX[0] = numBlocksWide / 2;
            snakeY[0] = numBlocksHigh / 2;
            //Then the body
            snakeX[1] = snakeX[0];
            snakeY[1] = snakeY[0];
        }
        //generate food in random position
        public void getFood() {
            Random random = new Random();
            int maxFoodX = numBlocksWide - 4, maxFoodY = numBlocksHigh - 13;
            foodX = random.nextInt(maxFoodX) + 2;
            foodY = random.nextInt(maxFoodY) + 2;
            //check if generated food overlay the snake and generate new one if is true
            for(int i = 0; i <= snakeLength; ++i) {
                if(snakeX[i] == foodX && snakeY[i] == foodY) {
                    getFood();
                }
            }
        }

        @Override
        public void run() {
            while (playingSnake) {
                updateGame();
                drawGame();
                controlFPS();
            }
        }

        public void updateGame() {
            //did the player eat the food?
            if(snakeX[0] == foodX && snakeY[0] == foodY) {
                //grow the snake
                snakeLength++;
                //generate new food
                getFood();
                //add to the score
                score = score + snakeLength;
            }
            //move the body - starting at the back
            for(int i = snakeLength; i > 0 ; --i) {
                snakeX[i] = snakeX[i - 1];
                snakeY[i] = snakeY[i - 1];
            }
            //move the head in the appropriate direction
            switch (directionOfTravel) {
                //go up
                case 0:
                    snakeY[0]  --;
                    break;
                //go right
                case 1:
                    snakeX[0] ++;
                    break;
                // go down
                case 2:
                    snakeY[0] ++;
                    break;
                //go left
                case 3:
                    snakeX[0] --;
                    break;
            }
            //no walls
            if(snakeX[0] == 1) {
                snakeX[0] = numBlocksWide - 3;
            }
            if(snakeX[0] >= numBlocksWide - 2) {
                snakeX[0] = 2;
            }
            if(snakeY[0] == -2) {
                snakeY[0] = numBlocksHigh - 7;
            }
            if(snakeY[0] == numBlocksHigh - 6) {
                snakeY[0] = -1;
            }
            //eaten ourselves?
            boolean dead = false;
            for (int i = snakeLength -1 ; i > 0; i--) {
                if ((i > 1) && (snakeX[0] == snakeX[i]) && (snakeY[0] == snakeY[i])) {
                    dead = true;
                    break;
                }
            }
            if(dead) {
                //exit to gameMenu
                Intent intent = new Intent(Level1.this, MainActivity.class);
                startActivity(intent);
                updatePlayerStats();
            }
            //change head bitmap
            if(directionOfTravel == 0) {
                headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_up);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
            } else if(directionOfTravel == 1) {
                headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_right);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
            } else if(directionOfTravel == 2) {
                headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_down);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
            } else {
                headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_left);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
            }
            //change head bitmap open the mouth
            if(foodY - snakeY[0] < 3 && foodY - snakeY[0] > -3 && foodX - snakeX[0] < 3 && foodX - snakeX[0] > -3 && directionOfTravel == 0) {
                headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_up_open);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
            } else if(foodY - snakeY[0] < 3 && foodY - snakeY[0] > -3 && foodX - snakeX[0] < 3 && foodX - snakeX[0] > -3 && directionOfTravel == 2) {
                headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_down_open);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
            } else if(foodY - snakeY[0] < 3 && foodY - snakeY[0] > -3 && foodX - snakeX[0] < 3 && foodX - snakeX[0] > -3 && directionOfTravel == 1) {
                headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_right_open);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
            } else if(foodY - snakeY[0] < 3 && foodY - snakeY[0] > -3 && foodX - snakeX[0] < 3 && foodX - snakeX[0] > -3 && directionOfTravel == 3) {
                headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_left_open);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
            }
        }

        public void drawGame() {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                canvas.drawBitmap(field,0,0,paint);
                //draw top and bottom lines
                for(int i = 0; i <= numBlocksWide; ++i) {
                    for(int j = 0; j <= numBlocksHigh; ++j) {
                        if(j <= 2 || j >= numBlocksHigh - 2) {
                            canvas.drawBitmap(green, i * blockSize, j * blockSize, paint);
                        }
                    }
                }
                // left and right lines
                for(int i = 0; i <= numBlocksWide; ++i) {
                    for(int j = 0; j <= numBlocksHigh; ++j) {
                        if(i <= 1 || i >= numBlocksWide - 2) {
                            canvas.drawBitmap(green, i * blockSize, j * blockSize, paint);
                        }
                    }
                }
                //draw trees
                canvas.drawBitmap(leftUpTree, -blockSize , -(blockSize * 2), paint);
                canvas.drawBitmap(rightUpTree, (numBlocksWide - 2) * blockSize , -(blockSize * 2), paint);
                canvas.drawBitmap(leftDownTree, -(blockSize * 2), (numBlocksHigh - 3) * blockSize , paint);
                canvas.drawBitmap(rightDownTree, (numBlocksWide -3) * blockSize, (numBlocksHigh -3 ) * blockSize, paint);
                //draw the food
                canvas.drawBitmap(foodBitmap, foodX * blockSize, (foodY * blockSize) + (blockSize * 4), paint);
                //draw the head of the snake
                canvas.drawBitmap(headBitmap, snakeX[0] * blockSize, (snakeY[0] * blockSize) + (blockSize * 4), paint);
                //draw the body
                for(int i = 1; i < snakeLength; i++) {
                    canvas.drawBitmap(bodyBitmap, snakeX[i] * blockSize, (snakeY[i] * blockSize) + (blockSize * 4), paint);
                }
                //draw edges with grass
                canvas.drawBitmap(upSide, 0, 3 * blockSize, paint); // up
                canvas.drawBitmap(downSide, blockSize * 2, (numBlocksHigh - 3) * blockSize, paint); //down
                canvas.drawBitmap(leftSide, blockSize * 2, 3 * blockSize, paint); //left
                canvas.drawBitmap(rightSide, (numBlocksWide - 3) * blockSize, 3 * blockSize, paint); //right
                // draw the Score
                paint.setColor(Color.rgb(251, 222, 154));
                paint.setTextSize(blockSize);
                paint.setFakeBoldText(true);
                canvas.drawText("Score: " + score, blockSize * 3, blockSize * 2, paint);
                canvas.drawText("Hi Score: " + hiScore, blockSize * (numBlocksWide / 2), blockSize * 2, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = gameSpeed - timeThisFrame;
            if (timeThisFrame > 0) {
                fps = (int) (1000 / timeThisFrame);
            }
            if (timeToSleep > 0) {
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    Log.e("error", "failed");
                }
            }
            lastFrameTime = System.currentTimeMillis();
        }

        public void pause() {
            playingSnake = false;
            try {
                ourThread.join();
            } catch (InterruptedException ignored) {
            }
        }

        public void resume() {
            playingSnake = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                if (motionEvent.getX() >= screenWidth / 2) {
                    //turn right
                    directionOfTravel++;
                    if (directionOfTravel == 4) {//no such direction
                        //loop back to 0(up)
                        directionOfTravel = 0;
                    }
                } else {
                    //turn left
                    directionOfTravel--;
                    if (directionOfTravel == -1) {//no such direction
                        //loop back to 0(up)
                        directionOfTravel = 3;
                    }
                }
            }
            return true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        playGroundView.pause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playGroundView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playGroundView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onPause();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    }

    public void configureDisplay() {
        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        //Set how many game blocks will fit into the height and width
        numBlocksWide = 20;
        //Set the size of each block on the game board. Increasing the divider will lower the elements size
        blockSize = screenWidth / numBlocksWide;
        //divide the height in square blocks
        numBlocksHigh = screenHeight / blockSize;
        //Load bitmaps
        headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_up);
        bodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.body);
        foodBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        field = BitmapFactory.decodeResource(getResources(), R.mipmap.field);
        green = BitmapFactory.decodeResource(getResources(), R.mipmap.green);
        leftUpTree = BitmapFactory.decodeResource(getResources(), R.drawable.left_up_tree);
        leftDownTree = BitmapFactory.decodeResource(getResources(), R.drawable.left_down_tree);
        rightUpTree = BitmapFactory.decodeResource(getResources(), R.drawable.right_up_tree);
        rightDownTree = BitmapFactory.decodeResource(getResources(), R.drawable.right_down_tree);
        upSide = BitmapFactory.decodeResource(getResources(), R.drawable.up_side);
        downSide = BitmapFactory.decodeResource(getResources(), R.drawable.down_side);
        leftSide = BitmapFactory.decodeResource(getResources(), R.drawable.left_side);
        rightSide = BitmapFactory.decodeResource(getResources(), R.drawable.right_side);
        //scale the bitmaps to match the block size
        headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
        bodyBitmap = Bitmap.createScaledBitmap(bodyBitmap, blockSize, blockSize, false);
        foodBitmap = Bitmap.createScaledBitmap(foodBitmap, blockSize, blockSize, false);
        field = Bitmap.createScaledBitmap(field, screenWidth, screenHeight, false);
        green = Bitmap.createScaledBitmap(green, blockSize, blockSize, false);
        leftUpTree = Bitmap.createScaledBitmap(leftUpTree, blockSize * 4, blockSize * 4, false);
        leftDownTree = Bitmap.createScaledBitmap(leftDownTree, blockSize * 4, blockSize * 4, false);
        rightUpTree = Bitmap.createScaledBitmap(rightUpTree, blockSize * 4, blockSize * 4, false);
        rightDownTree = Bitmap.createScaledBitmap(rightDownTree, blockSize * 4, blockSize * 4, false);
        upSide = Bitmap.createScaledBitmap(upSide, blockSize * numBlocksWide, blockSize, false);
        downSide = Bitmap.createScaledBitmap(downSide, blockSize * numBlocksWide, blockSize, false);
        leftSide = Bitmap.createScaledBitmap(leftSide, blockSize, blockSize * numBlocksHigh, false);
        rightSide = Bitmap.createScaledBitmap(rightSide, blockSize, blockSize * numBlocksHigh, false);
    }

    private void updatePlayerHiScore() {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).getPlayerHiScore(playerId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200) {
                    if (response.body().getStatus().equals("ok")) {
                        if (response.body().getResultCode() == 1) {
                            hiScore = response.body().getHiScore();
                            Log.d("UPDATE PLAYER HI SCORE", "Player id" + playerId + " Hi Score updated " + hiScore);
                        }
                    } else if (response.body().getStatus().equals("failed")) {
                        if (response.body().getResultCode() == 2) {
                            Log.d("UPDATE PLAYER HI SCORE", "Id not found. Player id " + playerId);
                        } else if (response.body().getResultCode() == 3) {
                            Log.d("UPDATE PLAYER HI SCORE", "SQl error");
                        }
                    }
                } else {
                    Log.d("UPDATE PLAYER HI SCORE", "Error connecting to database");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("UPDATE PLAYER HI SCORE", "Api Call fail. The error is: " + t);
            }
        });
    }

    private void updatePlayerStats() {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).updatePlayerStats(playerId, score);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200) {
                    if (response.body().getStatus().equals("ok")) {
                        if (response.body().getResultCode() == 1) {
                            Log.d("UPDATE PLAYER STATS", "Player id " + playerId + " .Game played increased. Hi Score updated " + hiScore);
                        } else {
                            Log.d("UPDATE PLAYER STATS", "Player id " + playerId + " .Game played increased.");
                        }
                    } else if (response.body().getStatus().equals("failed")) {
                        if (response.body().getResultCode() == 3) {
                            Log.d("UPDATE PLAYER STATS", "Player id " + playerId + " Failed to update Hi Score and Games played");
                        } else if (response.body().getResultCode() == 4) {
                            Log.d("UPDATE PLAYER STATS", "Player id " + playerId + " Failed to update Games played");
                        } else if (response.body().getResultCode() == 5) {
                            Log.d("UPDATE PLAYER STATS", "Player id " + playerId + " .No player found with this name");
                        } else if (response.body().getResultCode() == 6) {
                            Log.d("UPDATE PLAYER STATS", "SQl error");
                        }
                    }
                } else {
                    Log.d("UPDATE PLAYER STATS", "Cannot connect to database");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("UPDATE PLAYER STATS", "Api Call fail. The error is: " + t);
            }
        });
    }
}