package kr.or.mrhi.mp3project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.io.IOException;
import java.util.ArrayList;

import kr.or.mrhi.mp3project.Fragments.FavoriteFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyRecyclerAdapter.MusicSelectedListener {
    //xml main area widget id
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    //xml drawer id
    private DrawerLayout drawerLayout;
    private LinearLayout drawer;
    private ImageView pre, play, pause, next, back, repeat, thumbUp;
    private SeekBar seekBar;
    private TextView tv_title;

    // java 전역 변수
    private int index = -1;
    private boolean isPlaying = true;
    private String[] array = new String[]{"가수", "최신", "관심","랜덤"};
    private int[] IconArray = new int[]{R.drawable.ic_baseline_album_24, R.drawable.ic_baseline_calendar_today_24, R.drawable.ic_baseline_star_24,R.drawable.ic_baseline_shuffle_24};
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<MusicData> arrayList = new ArrayList<>();
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // loading activity
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        // ContentProvider를 통해 외부의 음악파일을 가져와 저장
        getMusicList();
        //위젯들의 이벤트 처리
        setWigetSetting();
        //ViewPager2 화면에 붙이기
        setViewPager2();
    }

    private void startMusic(int position) {
        isPlaying = false;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String path = arrayList.get(position).getID();
        if (path != null) {
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + path);
            mediaPlayer.stop();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(this, musicURI);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            tv_title.setText(arrayList.get(position).getTitle());
            drawerLayout.openDrawer(drawer);
            pause.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);
        }
    }

    private void setSeekBar() {
        isPlaying = true;
        seekBar.setProgress(0);
        seekBar.setMax(mediaPlayer.getDuration());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isPlaying) {
                    try {
                        Thread.sleep(200);
                        if (mediaPlayer != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e("ProgressUpdate", e.getMessage());
                    }
                }
                Log.d("스레드", "끝남");
            }
        }).start();
    }

    private void getCoverArtImage() {
        String result = getCoverArtPath(Long.parseLong(arrayList.get(index).getAlbumID()), this);
        ImageView album = findViewById(R.id.album);
        if (result != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(result);
            album.setImageBitmap(bitmap);
        } else {
            album.setImageResource(R.drawable.coconut);
        }
    }

    private static String getCoverArtPath(long albumId, Context context) {
        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART}, // 이미지 위치를 줌
                MediaStore.Audio.Albums._ID + " = ?", // 앨범 아이디가 ?인 곳에서
                new String[]{Long.toString(albumId)}, // ?에 들어갈 값
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }

    // 콜백 함수 overriding
    @Override
    public void onMusicSelected(ArrayList<MusicData> arrayList, int position) {
        if (index == -1 || !arrayList.get(position).getID().equals(this.arrayList.get(index).getID())) {
            this.arrayList = arrayList;
            index = position;
            startMusic(index);
            getCoverArtImage();
            setThumbUpImage();
            setSeekBar();
        } else {
            drawerLayout.openDrawer(drawer);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();
                break;
            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                break;
            case R.id.pre:
                if (index - 1 >= 0) {
                    index--;
                    startMusic(index);
                    getCoverArtImage();
                    setThumbUpImage();
                    setSeekBar();
                }
                break;
            case R.id.next:
                if (index + 1 < arrayList.size()) {
                    index++;
                    startMusic(index);
                    getCoverArtImage();
                    setThumbUpImage();
                    setSeekBar();
                }
                break;
            case R.id.iv_back:
                drawerLayout.closeDrawers();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;
            case R.id.thumbUp:
                if (arrayList.get(index).isThumbUp()) {
                    changeThumbUp(false);
                    Toast.makeText(getApplicationContext(), "관심목록에서 해제되었습니다", Toast.LENGTH_SHORT).show();
                    thumbUp.setImageResource(R.drawable.ic_baseline_star_outline_24);
                } else {
                    changeThumbUp(true);
                    Toast.makeText(getApplicationContext(), "관심목록에 추가되었습니다", Toast.LENGTH_SHORT).show();
                    thumbUp.setImageResource(R.drawable.ic_baseline_star_24);
                }
                break;
            case R.id.repeat:
                if(mediaPlayer.isLooping()){
                    mediaPlayer.setLooping(false);
                    repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
                }else{
                    mediaPlayer.setLooping(true);
                    repeat.setImageResource(R.drawable.ic_baseline_repeat_one_24);
                }
                break;
        }
    }

    private void changeThumbUp(boolean thumbUp) {
        arrayList.get(index).setThumbUp(thumbUp);
        mySQLiteOpenHelper.updateTbl(arrayList.get(index).getID(), thumbUp);
    }

    private void setThumbUpImage() {
        if (arrayList.get(index).isThumbUp()) {
            thumbUp.setImageResource(R.drawable.ic_baseline_star_24);
        } else {
            thumbUp.setImageResource(R.drawable.ic_baseline_star_outline_24);
        }
    }

    private void setViewPager2() {
        MyFragmentStateAdapter myFragmentStateAdapter = new MyFragmentStateAdapter(this);
        viewPager2.setAdapter(myFragmentStateAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(array[position]);
                tab.setIcon(IconArray[position]);
            }
        });
        tabLayoutMediator.attach();
    }

    private void setWigetSetting() {
        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        drawerLayout = findViewById(R.id.drawer_musicPlayer);
        pre = findViewById(R.id.pre);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        back = findViewById(R.id.iv_back);
        repeat = findViewById(R.id.repeat);
        seekBar = findViewById(R.id.seekbar);
        tv_title = findViewById(R.id.tv_title);
        thumbUp = findViewById(R.id.thumbUp);
        drawer = findViewById(R.id.drawer);

        pre.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
        repeat.setOnClickListener(this);
        thumbUp.setOnClickListener(this);

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                viewPager2.setUserInputEnabled(false);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                viewPager2.setUserInputEnabled(true);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if (seekBar.getProgress() > 0 && play.getVisibility() == View.GONE) {
                    mediaPlayer.start();
                }else if(seekBar.getProgress() <=0 && play.getVisibility() == View.GONE){
                    seekBar.setProgress(0);
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (++index < arrayList.size()) {
                    startMusic(index);
                    getCoverArtImage();
                    setThumbUpImage();
                    setSeekBar();
                } else {
                    index = 0;
                    startMusic(index);
                    getCoverArtImage();
                    setThumbUpImage();
                    setSeekBar();
                }
            }
        });
    }

    private void getMusicList() {
//        mySQLiteOpenHelper.initTbl();
        Cursor cursor = null;
        // 음원 파일 아이디, 앨범 아이디, 타이틀, 가수, 발매 년도
        String[] colums = new String[]{MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR};

        try {
            cursor = getContentResolver()
                    .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, colums, MediaStore.Audio.Media.DATA + " like ? ",
                            new String[]{"%mymusic%"}, MediaStore.Audio.Media.TITLE);

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
                boolean thumbUp = false;
                MusicData musicData = new MusicData(id, albumId, title, artist, year, thumbUp);
                mySQLiteOpenHelper.insertTbl(musicData);
            }
        } catch (Exception e) {
            Log.e("음악 플레이어", "getMusicList() 오류" + e.toString());
        } finally {
            if (cursor != null) cursor.close();
        }
    }// end of getMusicList()

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

