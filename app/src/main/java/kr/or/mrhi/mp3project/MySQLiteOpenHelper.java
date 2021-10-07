package kr.or.mrhi.mp3project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    //현재 활성화된 액티비티의 정보를 줘라
    private Context context;

    // name: DB이름
    // 데이터베이스가 없으면 만들고 있으면 만들지 않는다
    public MySQLiteOpenHelper(@Nullable Context context) {
        super(context, "MusicDB", null, 1);
        this.context = context;
        Log.d("가수 데이터베이스", "MySQLiteOpenHelper 생성자");
    }


    public boolean initTbl(){
        SQLiteDatabase sqlDB = getWritableDatabase();
        boolean flag = false;
        try{
            sqlDB = getWritableDatabase();
            // 기존의 테이블을 지우고 새로운 테이블을 만든다
            onUpgrade(sqlDB,1,2);
            flag = true;
        }catch (Exception e){
            Log.d("가수 데이터베이스", "테이블 생성 오류" + e.toString());
        }finally {
            sqlDB.close();
        }
        return flag;
    }

    // 테이블 생성 (테이블이 있다면 만들지 않는다)
    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL("create table musicTBL(" +
                "ID char(50)," +
                "AlbumID char(50)," +
                "title char(20)," +
                "artist char(10)," +
                "year char(10)," +
                "thumbUp boolean);");
        Log.d("가수 데이터베이스", "MySQLiteOpenHelper onCreate");
    }

    // 테이블 삭제하고 다시 만들기
    @Override
    public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion) {
        sdb.execSQL("drop table if exists musicTBL");
        onCreate(sdb);
    }

    public boolean insertTbl(MusicData musicData) {
        SQLiteDatabase sqlDB = getWritableDatabase();

        String ID = musicData.getID();
        String albumID = musicData.getAlbumID();
        String title = musicData.getTitle();
        String artist = musicData.getArtist();
        String year = musicData.getYear();
        boolean thumbUp = musicData.isThumbUp();

        title.replaceAll("'", "''");
        artist.replaceAll("'", "''");
        boolean flag = false;
        try {
            sqlDB = getWritableDatabase();
            String queryStr = String.format("insert into musicTbl values('%s','%s','%s','%s','%s','%b');", ID, albumID, title, artist, year, thumbUp);
            sqlDB.execSQL(queryStr);
            flag = true;
            Log.d("가수 데이터베이스", "테이블 insert 성공" + title + " " + year + " " + thumbUp);
        } catch (Exception e) {
            Log.d("가수 데이터베이스", "테이블 insert 오류" + e.toString());
        } finally {
            sqlDB.close();
        }
        return flag;
    }

    public void selectTbl(ArrayList<MusicData> arrayList, int number) {
        String sql = null;
        switch (number) {
            case 0:
                sql = "select * from musictbl order by artist;";
                break;
            case 1:
                sql = "select * from musictbl order by year desc;";
                break;
            case 2:
                sql = "select * from musictbl where thumbUp = 'true';";
                break;
            case 3:
                sql = "select * from musictbl order by random();";
        }
        loadData(arrayList, sql);
    }

    public void searchTbl(ArrayList<MusicData> arrayList, String text){
        String sql = "select * from musictbl WHERE title LIKE" + " '%" + text + "%'" + " OR artist LIKE" + " '%" + text + "%';";
        loadData(arrayList, sql);
    }

    public void loadData(ArrayList<MusicData> arrayList, String sql) {
        SQLiteDatabase sqlDB = null;
        Cursor cursor = null;
        try {
            sqlDB = getWritableDatabase();
            Log.d("가수", "받음");
            cursor = sqlDB.rawQuery(sql, null);
            arrayList.clear(); // 이미 저장했던 값을 다시 저장하지 않기 위해 값을 비움

            // 커서가 가리키는 테이블 셋의 값들을 받아와서 리스트에 저장
            while (cursor.moveToNext()) {
                String ID = cursor.getString(0);
                String albumID = cursor.getString(1);
                String title = cursor.getString(2);
                String artist = cursor.getString(3);
                String year = cursor.getString(4);
                boolean thumbUp = (cursor.getString(5)).equals("true");
                MusicData musicData = new MusicData(ID, albumID, title, artist, year, thumbUp);
                arrayList.add(musicData);
//                Log.d("가수 데이터베이스", "로드함" + thumbUp);
            }
        } catch (Exception e) {
            Log.d("가수 데이터베이스", "GroupData 로드 오류" + e.toString());
        } finally {
            // 다쓴 객체들은 닫아준다. 선언의 역순
            cursor.close();
            sqlDB.close();
        }
    }

    public boolean updateTbl(String id, boolean thumbUp){
        SQLiteDatabase sqlDB = getWritableDatabase();
        boolean flag = false;
        try {
            String queryStr = String.format("update musicTbl set thumbUp = '%b' where id = '%s';",thumbUp,id);
            sqlDB.execSQL(queryStr);
            Log.d("좋아요", "수정 성공");
            flag = true;
        }catch(Exception e){
            Log.d("가수 데이터베이스", "테이블 update 오류" + e.toString());
        }finally {
            sqlDB.close();
        }
        return flag;
    }
}

