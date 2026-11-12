package com.example.filterview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickFilterView

 * 单选、单分组筛选控件
 * - 点击选项即触发 OnOptionSelectedListener 回调
 * - 无重置/确定按钮、无角标
 * - 选项支持自动填满一行（Flexbox 流式）或固定列数
 * - 弹窗高度自适应
 * - 所有样式均通过 Config 动态控制，无静态 selector 依赖
 */
public class QuickFilterView extends FrameLayout {

    // ======================== 数据结构 ========================

    public static class FilterOption {
        public String text;
        public Object value;

        public FilterOption(String text, Object value) {
            this.text = text;
            this.value = value;
        }
    }

    public static class FilterGroup {
        public String title;
        public List<FilterOption> options = new ArrayList<>();

        public FilterGroup(String title) {
            this.title = title;
        }

        public FilterGroup addOption(String text, Object value) {
            options.add(new FilterOption(text, value));
            return this;
        }
    }

    // ======================== 回调 ========================

    public interface OnOptionSelectedListener {
        void onOptionSelected(FilterGroup group, FilterOption option, int position);
    }

    // ======================== 成员变量 ========================

    private QuickFilterConfig mConfig;
    private TextView tvTrigger;
    private BottomSheetDialog sheetDialog;
    private FilterGroup mGroup;
    private int mSelectedPosition = 0;
    private OnOptionSelectedListener mListener;

    // ======================== 构造 ========================

    public QuickFilterView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public QuickFilterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public QuickFilterView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // ======================== 初始化 ========================

    private void init(Context context) {
        mConfig = QuickFilterConfig.builder().build();

        LayoutInflater.from(context).inflate(R.layout.layout_view_filter, this, true);
        tvTrigger = findViewById(R.id.tv_trigger);

        applyTriggerStyle();
        tvTrigger.setOnClickListener(v -> showFilterSheet());
    }

    // ======================== 对外 API ========================

    public void applyConfig(@NonNull QuickFilterConfig config) {
        mConfig = config;
        applyTriggerStyle();
    }

    public void setFilterGroup(@NonNull FilterGroup group) {
        mGroup = group;
    }

    /**
     * 支持直接传入 Map 构造数据
     * key = 显示文本 (String)
     * value = 选项值 (Object)
     */
    public void setFilterGroup(String groupTitle, java.util.Map<String, ?> map) {
        FilterGroup group = new FilterGroup(groupTitle);
        if (map != null) {
            for (java.util.Map.Entry<String, ?> entry : map.entrySet()) {
                group.addOption(entry.getKey(), entry.getValue());
            }
        }
        mGroup = group;
    }

    /**
     * 文本数组 + 值数组，一一对应
     */
    public void setFilterGroup(String groupTitle, String[] texts, Object[] values) {
        FilterGroup group = new FilterGroup(groupTitle);
        if (texts != null && values != null) {
            int count = Math.min(texts.length, values.length);
            for (int i = 0; i < count; i++) {
                group.addOption(texts[i], values[i]);
            }
        }
        mGroup = group;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        mListener = listener;
    }

    // ======================== 触发按钮样式 ========================

    private void applyTriggerStyle() {
        if (tvTrigger == null) return;

        // triggerText
        tvTrigger.setText(mConfig.triggerText);

        // triggerTextColor
        tvTrigger.setTextColor(getResources().getColor(mConfig.triggerTextColor));

        // triggerTextSizeSp
        tvTrigger.setTextSize(TypedValue.COMPLEX_UNIT_SP, mConfig.triggerTextSizeSp);

        // triggerBgColor + triggerCornerRadiusDp
        GradientDrawable triggerBg = new GradientDrawable();
        triggerBg.setColor(getResources().getColor(mConfig.triggerBgColor));
        triggerBg.setCornerRadius(dp2px(mConfig.triggerCornerRadiusDp));
        tvTrigger.setBackground(triggerBg);

        // triggerAutoAdjustHeight / triggerFixedHeightDp
        if (mConfig.triggerAutoAdjustHeight) {
            tvTrigger.setPadding(
                    dp2px(mConfig.optionPaddingHorizontalDp),
                    dp2px(mConfig.optionPaddingVerticalDp),
                    dp2px(mConfig.optionPaddingHorizontalDp),
                    dp2px(mConfig.optionPaddingVerticalDp)
            );
            tvTrigger.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            tvTrigger.setPadding(dp2px(16), dp2px(8), dp2px(16), dp2px(8));
            tvTrigger.getLayoutParams().height = dp2px(mConfig.triggerFixedHeightDp);
        }
        tvTrigger.requestLayout();
    }

    // ======================== 弹窗 ========================

    public void showFilterSheet() {
        if (mGroup == null || mGroup.options.isEmpty()) return;

        if (sheetDialog == null) {
            initBottomSheet();
        }

        if (!sheetDialog.isShowing()) {
            buildOptions();

            if (mConfig.sheetHeightMode == QuickFilterConfig.SHEET_HEIGHT_ADAPTIVE) {
                applyAdaptiveHeight();
            } else {
                applyFixedPercentHeight();
            }

            sheetDialog.show();
        }
    }

    private void initBottomSheet() {
        sheetDialog = new BottomSheetDialog(getContext(), R.style.ThemeOverlay_Filter);
        sheetDialog.setContentView(R.layout.layout_filter_sheet);

        // closeIconColor + closeIconSizeDp
        ImageView ivClose = sheetDialog.findViewById(R.id.iv_close);
        if (ivClose != null) {
            ivClose.setColorFilter(getResources().getColor(mConfig.closeIconColor));
            ViewGroup.LayoutParams closeParams = ivClose.getLayoutParams();
            closeParams.width = dp2px(mConfig.closeIconSizeDp);
            closeParams.height = dp2px(mConfig.closeIconSizeDp);
            ivClose.setLayoutParams(closeParams);
            ivClose.setOnClickListener(v -> sheetDialog.dismiss());
        }

        // groupTitleTextSizeSp + groupTitleTextColor + groupTitlePaddingBottomDp
        TextView tvTitle = sheetDialog.findViewById(R.id.tv_sheet_title);
        if (tvTitle != null) {
            tvTitle.setText(mGroup.title);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mConfig.groupTitleTextSizeSp);
            tvTitle.setTextColor(getResources().getColor(mConfig.groupTitleTextColor));
            tvTitle.setPadding(
                    dp2px(0),
                    dp2px(0),
                    dp2px(0),
                    dp2px(mConfig.groupTitlePaddingBottomDp)
            );
        }

        // sheetBgColor + sheetTopCornerRadiusDp
        View sheetRoot = sheetDialog.findViewById(R.id.sheet_root);
        if (sheetRoot != null) {
            GradientDrawable sheetBg = new GradientDrawable();
            sheetBg.setColor(getResources().getColor(mConfig.sheetBgColor));
            sheetBg.setCornerRadii(new float[]{
                    dp2px(mConfig.sheetTopCornerRadiusDp),
                    dp2px(mConfig.sheetTopCornerRadiusDp),
                    dp2px(mConfig.sheetTopCornerRadiusDp),
                    dp2px(mConfig.sheetTopCornerRadiusDp),
                    0, 0, 0, 0
            });
            sheetRoot.setBackground(sheetBg);
        }

        // hideable = true（写死，Config 已删除此字段）
        sheetDialog.setCancelable(true);
        sheetDialog.setCanceledOnTouchOutside(true);
    }

    /** 构建选项视图 */
    private void buildOptions() {
        LinearLayout container = sheetDialog.findViewById(R.id.container_filters);
        if (container == null) return;
        container.removeAllViews();

        if (mConfig.optionColumnCount <= 0) {
            buildAutoColumnOptions(container);
        } else {
            buildFixedColumnOptions(container);
        }
    }

    /**
     * 自动模式（FlowLayout 流式布局，自动换行）
     * optionSpacingDp 生效
     */
    private void buildAutoColumnOptions(LinearLayout container) {
        FlowLayout flowLayout = new FlowLayout(getContext());
        flowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        int spacing = dp2px(mConfig.optionSpacingDp);

        for (int i = 0; i < mGroup.options.size(); i++) {
            final int position = i;
            FilterOption option = mGroup.options.get(i);

            TextView tv = createOptionView(option, position == mSelectedPosition);

            MarginLayoutParams params = new MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(spacing, spacing, 0, 0);
            tv.setLayoutParams(params);

            tv.setOnClickListener(v -> handleOptionClick(position));
            flowLayout.addView(tv);
        }

        container.addView(flowLayout);
    }

    /**
     * 固定列数模式
     * optionFixedColumnGapDp 生效
     */
    private void buildFixedColumnOptions(LinearLayout container) {
        int columnCount = mConfig.optionColumnCount;
        int gap = dp2px(mConfig.optionFixedColumnGapDp);

        LinearLayout currentRow = null;

        for (int i = 0; i < mGroup.options.size(); i++) {
            final int position = i;
            FilterOption option = mGroup.options.get(i);

            if (i % columnCount == 0) {
                currentRow = new LinearLayout(getContext());
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    rowParams.topMargin = gap;
                }
                container.addView(currentRow, rowParams);
            }

            TextView tv = createOptionView(option, position == mSelectedPosition);

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i % columnCount < columnCount - 1) {
                itemParams.rightMargin = gap;
            }
            tv.setLayoutParams(itemParams);

            tv.setOnClickListener(v -> handleOptionClick(position));
            currentRow.addView(tv);
        }
    }

    /** 创建单个选项 TextView —— 所有样式从 Config 动态读取 */
    private TextView createOptionView(FilterOption option, boolean isSelected) {
        TextView tv = new TextView(getContext());
        tv.setText(option.text);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setEllipsize(android.text.TextUtils.TruncateAt.END);

        // optionTextSizeSp
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mConfig.optionTextSizeSp);

        // optionPaddingHorizontalDp / optionPaddingVerticalDp
        tv.setPadding(
                dp2px(mConfig.optionPaddingHorizontalDp),
                dp2px(mConfig.optionPaddingVerticalDp),
                dp2px(mConfig.optionPaddingHorizontalDp),
                dp2px(mConfig.optionPaddingVerticalDp)
        );

        // optionBgSelected / optionBgNormal / optionCornerRadiusDp
        tv.setBackground(createOptionBackground());

        // optionTextColorSelected / optionTextColorNormal
        tv.setTextColor(createOptionTextColor());

        tv.setSelected(isSelected);
        return tv;
    }

    /** 动态创建选项背景（StateListDrawable + GradientDrawable） */
    private StateListDrawable createOptionBackground() {
        StateListDrawable sld = new StateListDrawable();

        // 选中状态
        GradientDrawable selected = new GradientDrawable();
        selected.setColor(getResources().getColor(mConfig.optionBgSelected));
        selected.setCornerRadius(dp2px(mConfig.optionCornerRadiusDp));
        sld.addState(new int[]{android.R.attr.state_selected}, selected);

        // 未选中状态
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(getResources().getColor(mConfig.optionBgNormal));
        normal.setCornerRadius(dp2px(mConfig.optionCornerRadiusDp));
        sld.addState(new int[]{}, normal);

        return sld;
    }

    /** 动态创建选项文字颜色（ColorStateList） */
    private ColorStateList createOptionTextColor() {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_selected},
                        new int[]{}
                },
                new int[]{
                        getResources().getColor(mConfig.optionTextColorSelected),
                        getResources().getColor(mConfig.optionTextColorNormal)
                }
        );
    }

    /** 选项点击处理 */
    private void handleOptionClick(int position) {
        mSelectedPosition = position;
        FilterOption selected = mGroup.options.get(position);

        // 更新触发按钮文案
        tvTrigger.setText(selected.text);

        // 回调
        if (mListener != null) {
            mListener.onOptionSelected(mGroup, selected, position);
        }

        // dismissOnOptionClick = true（写死）
        if (sheetDialog != null) {
            sheetDialog.dismiss();
        }
    }

    // ======================== 弹窗高度逻辑 ========================

    private void applyAdaptiveHeight() {
        View containerFilters = sheetDialog.findViewById(R.id.container_filters);
        if (containerFilters == null) return;

        containerFilters.post(() -> {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int defaultPeekHeight = (int) (screenHeight * (mConfig.peekHeightPercent / 100f));

            int contentHeight = containerFilters.getMeasuredHeight();
            int titleBarHeight = dp2px(50);
            int totalContentHeight = contentHeight + titleBarHeight + dp2px(16);

            BottomSheetBehavior<?> behavior = sheetDialog.getBehavior();

            if (totalContentHeight < defaultPeekHeight) {
                behavior.setPeekHeight(totalContentHeight);
                behavior.setFitToContents(true);
                behavior.setDraggable(false);
            } else {
                if (mConfig.sheetExpandMode == QuickFilterConfig.SHEET_EXPAND_FULL) {
                    behavior.setFitToContents(true);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setPeekHeight(defaultPeekHeight);
                    behavior.setFitToContents(false);
                }
                behavior.setDraggable(true);
            }
        });
    }

    private void applyFixedPercentHeight() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int peekHeight = (int) (screenHeight * (mConfig.peekHeightPercent / 100f));

        BottomSheetBehavior<?> behavior = sheetDialog.getBehavior();
        behavior.setPeekHeight(peekHeight);
        behavior.setFitToContents(false);
        behavior.setDraggable(true);
    }

    // ======================== 工具方法 ========================

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
