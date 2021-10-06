package kr.or.mrhi.mp3project;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private static final int MAX_IMAGE_SIZE = 240;
    private ArrayList<MusicData> arrayList;
    private Context context;
    private BitmapFactory.Options options = new BitmapFactory.Options();
    private MusicSelectedListener musicSelectedListener;

    public MyRecyclerAdapter(ArrayList<MusicData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.musicSelectedListener = (MusicSelectedListener) context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_artist.setText(arrayList.get(position).getArtist());
        Bitmap bitmap = getAlbumImage(context, Long.parseLong(arrayList.get(position).getAlbumID()), MAX_IMAGE_SIZE);
        if (bitmap != null) {
            //이미지가 들어있는 음원 파일일 경우
            holder.iv_album.setImageBitmap(bitmap);
        } else {
            //이미지가 없는 음원 파일일 경우
            holder.iv_album.setImageResource(R.drawable.island);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv_album;
        protected TextView tv_title, tv_artist;
        public MyViewHolder(@NonNull View view) {
            super(view);
            this.iv_album = view.findViewById(R.id.iv_album);
            this.tv_title = view.findViewById(R.id.tv_title);
            this.tv_artist = view.findViewById(R.id.tv_artist);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    musicSelectedListener.onMusicSelected(arrayList, getAdapterPosition());
                }
            });
        }
    }

    //callback
    public interface MusicSelectedListener{
        void onMusicSelected(ArrayList<MusicData> arrayList, int position);
    }

    private Bitmap getAlbumImage(Context context, long albumId, int maxImageSize) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + albumId);
        if (uri != null) {
            // uri가 null이 아닐경우에 이미지 가져오기 Parcelable을 사용한다
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
                //이미지를 로드하기 전에 부가적인 정보로 필터링하거나 추가하는 코드
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor(), null, options);
                int scale = 0;
                if (options.outHeight > maxImageSize || options.outWidth > maxImageSize) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                // 비트맵을 가져온다
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor(), null, options);
                if(bitmap != null){
                    //비트맵 사이즈를 체크해서 내가 정한 가로크기와 세로크기가 아니면 다시 비트맵 사이즈를 재설정
                    if(options.outHeight != maxImageSize || options.outWidth != maxImageSize){
                        Bitmap tempBitmap = Bitmap.createScaledBitmap(bitmap, maxImageSize, maxImageSize, true);
                        bitmap.recycle();
                        bitmap = tempBitmap;
                    }
                }
                return bitmap;
            } catch (FileNotFoundException e) {
                Log.e("음악플레이어", "비트맵 이미지 변환 오류" + e.toString());
            } finally {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
