package com.example.filterview;

import androidx.annotation.ColorRes;

/**
 * MultiFilterView 配置类（Builder 模式）

 * 特性：
 * - 多选、支持多分组
 * - 点击选项切换选中状态，不关闭弹窗
 * - 底部有重置 + 确定按钮
 * - 触发按钮可显示已选数量
 * - 所有样式均通过 Config 动态控制
 */
public class MultiFilterConfig {

    // ======================== 常量 ========================

    /** 弹窗高度：自适应 */
    public static final int SHEET_HEIGHT_ADAPTIVE = 0;
    /** 弹窗高度：固定百分比 */
    public static final int SHEET_HEIGHT_FIXED_PERCENT = 1;

    /** 弹窗展开：半展开 */
    public static final int SHEET_EXPAND_HALF = 0;
    /** 弹窗展开：全展开 */
    public static final int SHEET_EXPAND_FULL = 1;

    // ======================== 触发按钮 ========================

    /** 触发按钮默认文案（无选中项时显示） */
    public String triggerText = "筛选";

    /** 触发按钮文字颜色 */
    public @ColorRes int triggerTextColor = R.color.qf_text_primary;

    /** 触发按钮文字大小，单位 sp */
    public float triggerTextSizeSp = 14f;

    /** 触发按钮背景颜色 */
    public @ColorRes int triggerBgColor = R.color.qf_btn_confirm_bg;

    /** 触发按钮圆角，单位 dp */
    public float triggerCornerRadiusDp = 18f;

    /** 触发按钮是否自动调整高度 */
    public boolean triggerAutoAdjustHeight = true;

    /** 触发按钮固定高度，单位 dp（triggerAutoAdjustHeight=false 时生效） */
    public float triggerFixedHeightDp = 36f;

    /** 触发按钮在多选模式下显示选中数量（true="筛选 (3)"，false="筛选"） */
    public boolean triggerShowSelectedCount = true;

    /** 触发按钮选中数量后缀格式（%d 会被替换为数量） */
    public String triggerCountFormat = " (%d)";

    // ======================== 选项 ========================

    /** 选项列数：0=自动填满一行，>0=固定列数 */
    public int optionColumnCount = 0;

    /** 选项文字大小，单位 sp */
    public float optionTextSizeSp = 13f;

    /** 选项内边距，单位 dp */
    public int optionPaddingHorizontalDp = 12;
    public int optionPaddingVerticalDp = 8;

    /** 选项圆角，单位 dp */
    public float optionCornerRadiusDp = 16f;

    /** 选项选中背景色 */
    public @ColorRes int optionBgSelected = R.color.qf_option_bg_selected;

    /** 选项未选中背景色 */
    public @ColorRes int optionBgNormal = R.color.qf_option_bg_normal;

    /** 选项选中文字颜色 */
    public @ColorRes int optionTextColorSelected = R.color.qf_text_white;

    /** 选项未选中文字颜色 */
    public @ColorRes int optionTextColorNormal = R.color.qf_text_primary;

    /** 选项之间间距，单位 dp（自动模式生效） */
    public int optionSpacingDp = 8;

    /** 固定列数模式下列间距，单位 dp */
    public int optionFixedColumnGapDp = 8;

    /** 选项最大可选数量（0=不限制） */
    public int maxSelectCount = 0;

    /** 超过最大数量时的 Toast 提示（空=不提示，%d 替换为上限值） */
    public String overLimitToast = "最多选择 %d 项";

    // ======================== 分组标题 ========================

    /** 分组标题文字大小，单位 sp */
    public float groupTitleTextSizeSp = 15f;

    /** 分组标题文字颜色 */
    public @ColorRes int groupTitleTextColor = R.color.qf_text_primary;

    /** 分组标题底部间距（标题与选项之间的间距），单位 dp */
    public int groupTitlePaddingBottomDp = 8;

    /** 分组与分组之间的间距，单位 dp */
    public int groupSpacingDp = 24;

    // ======================== 底部按钮栏 ========================

    /** 确定按钮文案 */
    public String confirmButtonText = "确定";

    /** 确定按钮文字颜色 */
    public @ColorRes int confirmTextColor = R.color.qf_text_white;

    /** 确定按钮背景色 */
    public @ColorRes int confirmBgColor = R.color.qf_btn_confirm_bg;

    /** 确定按钮圆角，单位 dp */
    public float confirmCornerRadiusDp = 22f;

    /** 确定按钮高度，单位 dp */
    public float confirmButtonHeightDp = 44f;

    /** 确定按钮左右边距，单位 dp */
    public int confirmButtonMarginHorizontalDp = 16;

    /** 重置按钮是否显示 */
    public boolean resetButtonVisible = true;

    /** 重置按钮文案 */
    public String resetButtonText = "重置";

    /** 重置按钮文字颜色 */
    public @ColorRes int resetTextColor = R.color.qf_text_primary;

    /** 重置按钮背景色（透明=仅文字按钮） */
    public @ColorRes int resetBgColor = android.R.color.transparent;

    /** 重置按钮圆角，单位 dp */
    public float resetCornerRadiusDp = 22f;

    /** 重置按钮高度，单位 dp */
    public float resetButtonHeightDp = 44f;

    /** 底部按钮栏顶部分割线颜色（0=不显示） */
    public @ColorRes int buttonBarDividerColor = android.R.color.transparent;

    /** 底部按钮栏内边距，单位 dp */
    public int buttonBarPaddingDp = 12;

    // ======================== 关闭按钮 ========================

    /** 关闭按钮图标颜色 */
    public @ColorRes int closeIconColor = R.color.qf_close_icon;

    /** 关闭按钮尺寸，单位 dp */
    public int closeIconSizeDp = 24;

    // ======================== 弹窗 ========================

    /** 弹窗高度模式 */
    public int sheetHeightMode = SHEET_HEIGHT_ADAPTIVE;

    /** 半展开高度占屏幕百分比 */
    public int peekHeightPercent = 75;

    /** 内容多时的展开方式 */
    public int sheetExpandMode = SHEET_EXPAND_HALF;

    /** 弹窗背景颜色 */
    public @ColorRes int sheetBgColor = R.color.qf_sheet_bg;

    /** 弹窗顶部圆角，单位 dp */
    public float sheetTopCornerRadiusDp = 20f;

    // ======================== Builder ========================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MultiFilterConfig c = new MultiFilterConfig();

        // 触发按钮
        public Builder triggerText(String t) { c.triggerText = t; return this; }
        public Builder triggerTextColor(@ColorRes int r) { c.triggerTextColor = r; return this; }
        public Builder triggerTextSize(float sp) { c.triggerTextSizeSp = sp; return this; }
        public Builder triggerBgColor(@ColorRes int r) { c.triggerBgColor = r; return this; }
        public Builder triggerCornerRadius(float dp) { c.triggerCornerRadiusDp = dp; return this; }
        public Builder triggerFixedHeight(float dp) { c.triggerFixedHeightDp = dp; c.triggerAutoAdjustHeight = false; return this; }
        public Builder triggerShowSelectedCount(boolean show) { c.triggerShowSelectedCount = show; return this; }
        public Builder triggerCountFormat(String format) { c.triggerCountFormat = format; return this; }

        // 选项
        public Builder optionColumnCount(int count) { c.optionColumnCount = count; return this; }
        public Builder optionTextSize(float sp) { c.optionTextSizeSp = sp; return this; }
        public Builder optionBgSelected(@ColorRes int r) { c.optionBgSelected = r; return this; }
        public Builder optionBgNormal(@ColorRes int r) { c.optionBgNormal = r; return this; }
        public Builder optionTextColorSelected(@ColorRes int r) { c.optionTextColorSelected = r; return this; }
        public Builder optionTextColorNormal(@ColorRes int r) { c.optionTextColorNormal = r; return this; }
        public Builder optionSpacing(int dp) { c.optionSpacingDp = dp; return this; }
        public Builder optionFixedColumnGap(int dp) { c.optionFixedColumnGapDp = dp; return this; }
        public Builder maxSelectCount(int max) { c.maxSelectCount = max; return this; }
        public Builder overLimitToast(String msg) { c.overLimitToast = msg; return this; }

        // 分组标题
        public Builder groupTitleTextColor(@ColorRes int r) { c.groupTitleTextColor = r; return this; }
        public Builder groupTitlePaddingBottom(int dp) { c.groupTitlePaddingBottomDp = dp; return this; }
        public Builder groupSpacing(int dp) { c.groupSpacingDp = dp; return this; }

        // 确定按钮
        public Builder confirmButtonText(String t) { c.confirmButtonText = t; return this; }
        public Builder confirmTextColor(@ColorRes int r) { c.confirmTextColor = r; return this; }
        public Builder confirmBgColor(@ColorRes int r) { c.confirmBgColor = r; return this; }
        public Builder confirmCornerRadius(float dp) { c.confirmCornerRadiusDp = dp; return this; }
        public Builder confirmButtonHeight(float dp) { c.confirmButtonHeightDp = dp; return this; }

        // 重置按钮
        public Builder resetButtonVisible(boolean v) { c.resetButtonVisible = v; return this; }
        public Builder resetButtonText(String t) { c.resetButtonText = t; return this; }
        public Builder resetTextColor(@ColorRes int r) { c.resetTextColor = r; return this; }
        public Builder resetBgColor(@ColorRes int r) { c.resetBgColor = r; return this; }
        public Builder resetCornerRadius(float dp) { c.resetCornerRadiusDp = dp; return this; }
        public Builder resetButtonHeight(float dp) { c.resetButtonHeightDp = dp; return this; }

        // 按钮栏
        public Builder buttonBarDividerColor(@ColorRes int r) { c.buttonBarDividerColor = r; return this; }
        public Builder buttonBarPadding(int dp) { c.buttonBarPaddingDp = dp; return this; }

        // 关闭按钮
        public Builder closeIconColor(@ColorRes int r) { c.closeIconColor = r; return this; }
        public Builder closeIconSize(int dp) { c.closeIconSizeDp = dp; return this; }

        // 弹窗
        public Builder sheetHeightMode(int mode) { c.sheetHeightMode = mode; return this; }
        public Builder peekHeightPercent(int p) { c.peekHeightPercent = p; return this; }
        public Builder sheetExpandMode(int mode) { c.sheetExpandMode = mode; return this; }
        public Builder sheetTopCornerRadius(float dp) { c.sheetTopCornerRadiusDp = dp; return this; }

        public MultiFilterConfig build() { return c; }
    }
}