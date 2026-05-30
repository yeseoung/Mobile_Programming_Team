package com.example.drughelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Medicine> medicineList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    TextView warn;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    // ✨ 생성자 교정: 첫 번째 인자로 Context를 받아 mInflater를 안전하게 초기화합니다.
    public MyAdapter(Context context, List<Medicine> medicineList) {
        this.mInflater = LayoutInflater.from(context);
        this.medicineList = medicineList;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✨ 이제 mInflater가 null이 아니므로 에러 없이 item.xml 레이아웃을 가져옵니다.
        View view = mInflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Medicine medicine = medicineList.get(position);
        if (medicine == null) return;

        // 알약 이름 세팅
        holder.drugName.setText(medicine.getItemName());
        holder.notEat.setText(medicine.getDurNotice());
        if(!medicine.getDurNotice().equals("특이사항 없음")) {
            holder.notEat.setTextColor(Color.RED); // 금기 사항이 있다면 빨간색 강조
            holder.warn.setVisibility(View.VISIBLE);
        } else {
            holder.notEat.setTextColor(Color.GRAY);
            holder.warn.setVisibility(View.GONE);
        }
        // 기본 이미지 먼저 세팅 (다운로드 전 공백 방지)
        holder.drugImage.setImageResource(R.drawable.medicine);

        final String imgUrl = medicine.getImageUrl();
        holder.drugImage.setTag(imgUrl);

        // 이미지 비동기 다운로드 및 스크롤 튀는 현상 방지 로직
        if (imgUrl != null && !imgUrl.isEmpty() && !imgUrl.equals("null")) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = downloadBitmap(imgUrl);
                    if (bitmap != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                int currentPosition = holder.getBindingAdapterPosition();
                                if (currentPosition != RecyclerView.NO_POSITION) {
                                    if (imgUrl.equals(holder.drugImage.getTag())) {
                                        holder.drugImage.setImageBitmap(bitmap);
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private Bitmap downloadBitmap(String urlString) {
        try {
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            java.io.InputStream input = connection.getInputStream();
            return android.graphics.BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return medicineList != null ? medicineList.size() : 0;
    }

    public Medicine getItem(int position) {
        if (medicineList != null && position >= 0 && position < medicineList.size()) {
            return medicineList.get(position);
        }
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView drugName;
        ImageView drugImage;
        ImageView warn;
        TextView notEat;
        ViewHolder(View itemView) {
            super(itemView);
            // ✨ 뷰 매칭 (안 쓰는 myTextView와 불필요한 mData 매핑 코드는 정리했습니다)
            drugName = itemView.findViewById(R.id.info_text);
            drugImage = itemView.findViewById(R.id.drugImage);
            warn = itemView.findViewById(R.id.warn);
            notEat = itemView.findViewById(R.id.notEat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

