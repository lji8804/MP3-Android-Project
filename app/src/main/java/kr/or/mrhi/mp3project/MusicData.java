package kr.or.mrhi.mp3project;

import java.io.Serializable;

public class MusicData implements Serializable {
    private String ID;
    private String albumID;// 앨범 자켓 이미지 위치값만 가져옴
    private String title;
    private String artist;
    private String year;
    private boolean thumbUp;

    public MusicData(String ID, String albumID, String title, String artist, String year, boolean thumbUp) {
        this.ID = ID;
        this.albumID = albumID;
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.thumbUp = thumbUp;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isThumbUp() {
        return thumbUp;
    }

    public void setThumbUp(boolean thumbUp) {
        this.thumbUp = thumbUp;
    }

}
