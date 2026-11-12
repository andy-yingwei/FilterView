package com.example.myfilterview;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filterview.QuickFilterConfig;
import com.example.filterview.QuickFilterView;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuickFilterTestActivity extends AppCompatActivity {

    private static final String TAG = "QuickFilterTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        QuickFilterView filterView = findViewById(R.id.quick_filter);

        // ===== Config =====
        QuickFilterConfig config = new QuickFilterConfig.Builder()
                .triggerText("排序")
                .triggerCornerRadius(18)
                .triggerTextSize(14)
                .optionColumnCount(3)
                .optionSpacing(8)
                .optionFixedColumnGap(12)
                .optionTextSize(13)
                .optionBgNormal(android.R.color.white)
                .optionBgSelected(R.color.purple_200)
                .optionTextColorNormal(android.R.color.darker_gray)
                .optionTextColorSelected(R.color.white)
                .groupTitleTextColor(R.color.purple_500)
                .groupTitlePaddingBottom(8)
                .closeIconColor(R.color.purple_500)
                .closeIconSize(24)
                .sheetTopCornerRadius(20)
                .sheetHeightMode(QuickFilterConfig.SHEET_HEIGHT_ADAPTIVE)
                .peekHeightPercent(60)
                .sheetExpandMode(QuickFilterConfig.SHEET_EXPAND_HALF)
                .build();

        filterView.applyConfig(config);

        // ===== 数据 =====
        Map<String, Object> sortMap = new LinkedHashMap<>();
        sortMap.put("综合排序", "comprehensive");
        sortMap.put("最新发布", "latest");
        sortMap.put("最多播放", "most_played");
        sortMap.put("最多收藏", "most_fav");
        sortMap.put("最多评论", "most_comment");
        sortMap.put("评分最高", "highest_rating");
        sortMap.put("最多点赞", "most_like");
        sortMap.put("最近更新", "recently_updated");

        filterView.setFilterGroup("排序方式", sortMap);

        // 默认选中第1项（"最新发布"）
        filterView.setSelectedPosition(1);

        // ===== 回调 =====
        filterView.setOnOptionSelectedListener(new QuickFilterView.OnOptionSelectedListener() {
            @Override
            public void onOptionSelected(QuickFilterView.FilterGroup group,
                                         QuickFilterView.FilterOption option,
                                         int position) {

                Toast.makeText(QuickFilterTestActivity.this,
                        "选中: " + option.text + " (value=" + option.value + ")",
                        Toast.LENGTH_SHORT).show();

                Log.d(TAG, "===== onOptionSelected =====");
                Log.d(TAG, "group: " + group.title);
                Log.d(TAG, "option: " + option.text + ", value=" + option.value);
                Log.d(TAG, "position: " + position);
            }
        });
    }
}