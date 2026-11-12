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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiFilterView
 * <p>
 * 多分组筛选控件（每组单选，整体需确认）
 * - 每组内点击选项切换选中（该组只能选一个）
 * - 点击选项不关闭弹窗
 * - 底部有重置 + 确定按钮
 * - 触发按钮可显示已选数量
 * - 确认回调返回 List<GroupResult>，包含组名 + 选项
 */
public class MultiFilterView extends FrameLayout {

    // ======================== 回调 ========================

    /** 每组选中结果 */
    public static class GroupResult {
        public final String groupTitle;
        public final QuickFilterView.FilterOption option;

        public GroupResult(String groupTitle, QuickFilterView.FilterOption option) {
            this.groupTitle = groupTitle;
            this.option = option;
        }

        @Override
        public String toString() {
            return "GroupResult{group='" + groupTitle + "', option=" + option.text + "(" + option.value + ")}";
        }
    }

    public interface OnOptionsSelectedListener {
        void onOptionsSelected(List<GroupResult> results);
    }

    // ======================== 成员变量 ========================

    private MultiFilterConfig mConfig;
    private TextView tvTrigger;
    private BottomSheetDialog sheetDialog;

    /** 支持多分组 */
    private List<QuickFilterView.FilterGroup> mGroups = new ArrayList<>();

    /**
     * 选中状态：groupIndex → selectedPosition
     * 每组只能选一个（-1 = 未选）
     */
    private List<Integer> mSelectedPositions = new ArrayList<>();

    private OnOptionsSelectedListener mListener;

    // ======================== 构造 ========================

    public MultiFilterView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MultiFilterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultiFilterView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // ======================== 初始化 ========================

    private void init(Context context) {
        mConfig = MultiFilterConfig.builder().build();

        LayoutInflater.from(context).inflate(R.layout.layout_view_filter, this, true);
        tvTrigger = findViewById(R.id.tv_trigger);

        applyTriggerStyle();
        tvTrigger.setOnClickListener(v -> showFilterSheet());
    }

    // ======================== 对外 API ========================

    public void applyConfig(@NonNull MultiFilterConfig config) {
        mConfig = config;
        applyTriggerStyle();
    }

    public void setFilterGroup(@NonNull QuickFilterView.FilterGroup group) {
        mGroups.clear();
        mGroups.add(group);
        initSelectedPositions();
    }

    public void setFilterGroup(String groupTitle, java.util.Map<String, ?> map) {
        QuickFilterView.FilterGroup group = new QuickFilterView.FilterGroup(groupTitle);
        if (map != null) {
            for (java.util.Map.Entry<String, ?> entry : map.entrySet()) {
                group.addOption(entry.getKey(), entry.getValue());
            }
        }
        mGroups.clear();
        mGroups.add(group);
        initSelectedPositions();
    }

    public void setFilterGroup(String groupTitle, String[] texts, Object[] values) {
        QuickFilterView.FilterGroup group = new QuickFilterView.FilterGroup(groupTitle);
        if (texts != null && values != null) {
            int count = Math.min(texts.length, values.length);
            for (int i = 0; i < count; i++) {
                group.addOption(texts[i], values[i]);
            }
        }
        mGroups.clear();
        mGroups.add(group);
        initSelectedPositions();
    }

    public void setFilterGroups(@NonNull List<QuickFilterView.FilterGroup> groups) {
        mGroups.clear();
        mGroups.addAll(groups);
        initSelectedPositions();
    }

    private void initSelectedPositions() {
        mSelectedPositions.clear();
        for (int i = 0; i < mGroups.size(); i++) {
            mSelectedPositions.add(-1);
        }
        updateTriggerText();
    }

    public void setOnOptionsSelectedListener(OnOptionsSelectedListener listener) {
        mListener = listener;
    }

    // ======================== 触发按钮样式 ========================

    private void applyTriggerStyle() {
        if (tvTrigger == null) return;

        tvTrigger.setText(mConfig.triggerText);
        tvTrigger.setTextColor(getResources().getColor(mConfig.triggerTextColor));
        tvTrigger.setTextSize(TypedValue.COMPLEX_UNIT_SP, mConfig.triggerTextSizeSp);

        GradientDrawable triggerBg = new GradientDrawable();
        triggerBg.setColor(getResources().getColor(mConfig.triggerBgColor));
        triggerBg.setCornerRadius(dp2px(mConfig.triggerCornerRadiusDp));
        tvTrigger.setBackground(triggerBg);

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

        updateTriggerText();
    }

    private void updateTriggerText() {
        if (tvTrigger == null) return;
        int totalSelected = getTotalSelectedCount();
        if (totalSelected > 0 && mConfig.triggerShowSelectedCount) {
            tvTrigger.setText(mConfig.triggerText + String.format(mConfig.triggerCountFormat, totalSelected));
        } else {
            tvTrigger.setText(mConfig.triggerText);
        }
    }

    private int getTotalSelectedCount() {
        int count = 0;
        for (int pos : mSelectedPositions) {
            if (pos >= 0) count++;
        }
        return count;
    }

    // ======================== 弹窗 ========================

    public void showFilterSheet() {
        if (mGroups.isEmpty()) return;
        boolean hasOptions = false;
        for (QuickFilterView.FilterGroup g : mGroups) {
            if (!g.options.isEmpty()) { hasOptions = true; break; }
        }
        if (!hasOptions) return;

        if (sheetDialog == null) {
            initBottomSheet();
        }

        if (!sheetDialog.isShowing()) {
            buildGroups();

            if (mConfig.sheetHeightMode == MultiFilterConfig.SHEET_HEIGHT_ADAPTIVE) {
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

        ImageView ivClose = sheetDialog.findViewById(R.id.iv_close);
        if (ivClose != null) {
            ivClose.setColorFilter(getResources().getColor(mConfig.closeIconColor));
            ViewGroup.LayoutParams closeParams = ivClose.getLayoutParams();
            closeParams.width = dp2px(mConfig.closeIconSizeDp);
            closeParams.height = dp2px(mConfig.closeIconSizeDp);
            ivClose.setLayoutParams(closeParams);
            ivClose.setOnClickListener(v -> sheetDialog.dismiss());
        }

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

        sheetDialog.setCancelable(false);
        sheetDialog.setCanceledOnTouchOutside(false);
    }

    /** 构建多分组视图 */
    private void buildGroups() {
        LinearLayout container = sheetDialog.findViewById(R.id.container_filters);
        if (container == null) return;
        container.removeAllViews();

        for (int groupIndex = 0; groupIndex < mGroups.size(); groupIndex++) {
            QuickFilterView.FilterGroup group = mGroups.get(groupIndex);

            // 分组间距
            if (groupIndex > 0) {
                Space space = new Space(getContext());
                space.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mConfig.groupSpacingDp)));
                container.addView(space);
            }

            // 分组标题
            TextView tvTitle = new TextView(getContext());
            tvTitle.setText(group.title);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mConfig.groupTitleTextSizeSp);
            tvTitle.setTextColor(getResources().getColor(mConfig.groupTitleTextColor));
            tvTitle.setPadding(0, 0, 0, dp2px(mConfig.groupTitlePaddingBottomDp));
            container.addView(tvTitle);

            // 选项区域
            if (mConfig.optionColumnCount <= 0) {
                buildAutoColumnOptions(container, groupIndex, group);
            } else {
                buildFixedColumnOptions(container, groupIndex, group);
            }
        }

        // 底部按钮栏
        setupButtonBar(container);
    }

    /** 动态创建底部按钮栏 */
    private void setupButtonBar(LinearLayout parentContainer) {
        LinearLayout buttonBar = new LinearLayout(getContext());
        buttonBar.setOrientation(LinearLayout.HORIZONTAL);
        buttonBar.setGravity(Gravity.CENTER_VERTICAL);
        buttonBar.setPadding(
                dp2px(mConfig.buttonBarPaddingDp),
                dp2px(mConfig.buttonBarPaddingDp),
                dp2px(mConfig.buttonBarPaddingDp),
                dp2px(mConfig.buttonBarPaddingDp)
        );

        // 分割线
        if (mConfig.buttonBarDividerColor != android.R.color.transparent) {
            View divider = new View(getContext());
            divider.setBackgroundColor(getResources().getColor(mConfig.buttonBarDividerColor));
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp2px(1));
            dividerParams.setMargins(0, 0, 0, dp2px(mConfig.buttonBarPaddingDp));
            parentContainer.addView(divider, dividerParams);
        }

        // 重置按钮
        if (mConfig.resetButtonVisible) {
            Button btnReset = new Button(getContext(), null, 0);
            btnReset.setText(mConfig.resetButtonText);
            btnReset.setTextColor(getResources().getColor(mConfig.resetTextColor));
            btnReset.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            btnReset.setGravity(Gravity.CENTER);
            btnReset.setIncludeFontPadding(false);
            btnReset.setMinWidth(0);
            btnReset.setMinHeight(0);
            btnReset.setPadding(0, 0, 0, 0);

            GradientDrawable resetBg = new GradientDrawable();
            resetBg.setColor(mConfig.resetBgColor != android.R.color.transparent
                    ? getResources().getColor(mConfig.resetBgColor)
                    : android.graphics.Color.TRANSPARENT);
            resetBg.setCornerRadius(dp2px(mConfig.resetCornerRadiusDp));
            btnReset.setBackground(resetBg);

            LinearLayout.LayoutParams resetParams = new LinearLayout.LayoutParams(
                    0, dp2px(mConfig.resetButtonHeightDp), 1f);
            resetParams.rightMargin = dp2px(8);
            btnReset.setLayoutParams(resetParams);

            btnReset.setOnClickListener(v -> handleReset());
            buttonBar.addView(btnReset);
        }

        // 确定按钮
        Button btnConfirm = new Button(getContext(), null, 0);
        btnConfirm.setText(mConfig.confirmButtonText);
        btnConfirm.setTextColor(getResources().getColor(mConfig.confirmTextColor));
        btnConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        btnConfirm.setGravity(Gravity.CENTER);
        btnConfirm.setIncludeFontPadding(false);
        btnConfirm.setMinWidth(0);
        btnConfirm.setMinHeight(0);
        btnConfirm.setPadding(0, 0, 0, 0);

        GradientDrawable confirmBg = new GradientDrawable();
        confirmBg.setColor(getResources().getColor(mConfig.confirmBgColor));
        confirmBg.setCornerRadius(dp2px(mConfig.confirmCornerRadiusDp));
        btnConfirm.setBackground(confirmBg);

        int confirmWeight = mConfig.resetButtonVisible ? 1 : 2;
        LinearLayout.LayoutParams confirmParams = new LinearLayout.LayoutParams(
                0, dp2px(mConfig.confirmButtonHeightDp), confirmWeight);
        btnConfirm.setLayoutParams(confirmParams);

        btnConfirm.setOnClickListener(v -> handleConfirm());
        buttonBar.addView(btnConfirm);

        parentContainer.addView(buttonBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 自动模式（FlowLayout 流式布局）
     */
    private void buildAutoColumnOptions(LinearLayout container, int groupIndex, QuickFilterView.FilterGroup group) {
        FlowLayout flowLayout = new FlowLayout(getContext());
        flowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        int spacing = dp2px(mConfig.optionSpacingDp);

        for (int i = 0; i < group.options.size(); i++) {
            final int gIndex = groupIndex;
            final int position = i;
            QuickFilterView.FilterOption option = group.options.get(i);

            boolean isSelected = (mSelectedPositions.get(gIndex) == position);
            TextView tv = createOptionView(option, isSelected);

            MarginLayoutParams params = new MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(spacing, spacing, 0, 0);
            tv.setLayoutParams(params);

            tv.setOnClickListener(v -> handleOptionClick(gIndex, position));
            flowLayout.addView(tv);
        }

        container.addView(flowLayout);
    }

    /**
     * 固定列数模式
     */
    private void buildFixedColumnOptions(LinearLayout container, int groupIndex, QuickFilterView.FilterGroup group) {
        int columnCount = mConfig.optionColumnCount;
        int gap = dp2px(mConfig.optionFixedColumnGapDp);

        LinearLayout currentRow = null;

        for (int i = 0; i < group.options.size(); i++) {
            final int gIndex = groupIndex;
            final int position = i;
            QuickFilterView.FilterOption option = group.options.get(i);

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

            boolean isSelected = (mSelectedPositions.get(gIndex) == position);
            TextView tv = createOptionView(option, isSelected);

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i % columnCount < columnCount - 1) {
                itemParams.rightMargin = gap;
            }
            tv.setLayoutParams(itemParams);

            tv.setOnClickListener(v -> handleOptionClick(gIndex, position));
            currentRow.addView(tv);
        }
    }

    /** 创建单个选项 TextView */
    private TextView createOptionView(QuickFilterView.FilterOption option, boolean isSelected) {
        TextView tv = new TextView(getContext());
        tv.setText(option.text);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setEllipsize(android.text.TextUtils.TruncateAt.END);

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mConfig.optionTextSizeSp);
        tv.setPadding(
                dp2px(mConfig.optionPaddingHorizontalDp),
                dp2px(mConfig.optionPaddingVerticalDp),
                dp2px(mConfig.optionPaddingHorizontalDp),
                dp2px(mConfig.optionPaddingVerticalDp)
        );

        tv.setBackground(createOptionBackground());
        tv.setTextColor(createOptionTextColor());
        tv.setSelected(isSelected);
        return tv;
    }

    private StateListDrawable createOptionBackground() {
        StateListDrawable sld = new StateListDrawable();

        GradientDrawable selected = new GradientDrawable();
        selected.setColor(getResources().getColor(mConfig.optionBgSelected));
        selected.setCornerRadius(dp2px(mConfig.optionCornerRadiusDp));
        sld.addState(new int[]{android.R.attr.state_selected}, selected);

        GradientDrawable normal = new GradientDrawable();
        normal.setColor(getResources().getColor(mConfig.optionBgNormal));
        normal.setCornerRadius(dp2px(mConfig.optionCornerRadiusDp));
        sld.addState(new int[]{}, normal);

        return sld;
    }

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

    // ======================== 点击逻辑：每组单选 ========================

    /** 选项点击：每组只能选一个，直接替换 */
    private void handleOptionClick(int groupIndex, int position) {
        int currentSelected = mSelectedPositions.get(groupIndex);

        if (currentSelected == position) {
            // 再次点击同一项 → 取消选中
            mSelectedPositions.set(groupIndex, -1);
        } else {
            // 选中新项（自动取消同组旧项）
            mSelectedPositions.set(groupIndex, position);
        }

        refreshOptionsUI();
        updateTriggerText();
    }

    /** 刷新弹窗内选项视图 */
    private void refreshOptionsUI() {
        LinearLayout container = sheetDialog.findViewById(R.id.container_filters);
        if (container == null) return;
        buildGroups();
    }

    /** 重置：所有组清空选中 */
    private void handleReset() {
        for (int i = 0; i < mSelectedPositions.size(); i++) {
            mSelectedPositions.set(i, -1);
        }
        refreshOptionsUI();
        updateTriggerText();
    }

    /** 确认：收集每组已选项，附带组名 */
    private void handleConfirm() {
        List<GroupResult> results = new ArrayList<>();

        for (int gi = 0; gi < mGroups.size(); gi++) {
            int pos = mSelectedPositions.get(gi);
            if (pos >= 0 && pos < mGroups.get(gi).options.size()) {
                String groupTitle = mGroups.get(gi).title;
                QuickFilterView.FilterOption option = mGroups.get(gi).options.get(pos);
                results.add(new GroupResult(groupTitle, option));
            }
        }

        if (mListener != null) {
            mListener.onOptionsSelected(results);
        }

        if (sheetDialog != null) {
            sheetDialog.dismiss();
        }
    }

    // ======================== 弹窗高度 ========================

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
                if (mConfig.sheetExpandMode == MultiFilterConfig.SHEET_EXPAND_FULL) {
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

    // ======================== 工具 ========================

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
