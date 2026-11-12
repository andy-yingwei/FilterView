package com.example.filterview;

import androidx.annotation.ColorRes;

/**
 * QuickFilterView 配置类（Builder 模式）

 * 特性：
 * - 单选、单分组（但保留分组标题样式配置）
 * - 点击选项即确认回调
 * - 无重置/确定按钮、无角标
 * - 选项支持自动填满一行 或 固定列数
 * - 弹窗高度自适应（内容少=实际高度不可拖拽，内容多=半展开/全展开可滚动）
 */
public class QuickFilterConfig {

    // ======================== 常量 ========================

    /** 弹窗高度：自适应（内容少=实际高度，内容多=按 expandMode 展开） */
    public static final int SHEET_HEIGHT_ADAPTIVE = 0;
    /** 弹窗高度：固定百分比 */
    public static final int SHEET_HEIGHT_FIXED_PERCENT = 1;

    /** 弹窗展开：半展开（peekHeightPercent） */
    public static final int SHEET_EXPAND_HALF = 0;
    /** 弹窗展开：全展开 */
    public static final int SHEET_EXPAND_FULL = 1;

    // ======================== 触发按钮 ========================

    /** 触发按钮文案 */
    public String triggerText = "筛选";

    /** 触发按钮文字颜色 */
    public @ColorRes int triggerTextColor = R.color.qf_text_primary;

    /** 触发按钮文字大小，单位 sp */
    public float triggerTextSizeSp = 14f;

    /** 触发按钮背景颜色 */
    public @ColorRes int triggerBgColor = R.color.qf_btn_confirm_bg;

    /** 触发按钮圆角，单位 dp */
    public float triggerCornerRadiusDp = 18f;

    /** 触发按钮是否自动调整高度（true=根据文字自适应, false=使用固定高度） */
    public boolean triggerAutoAdjustHeight = true;

    /** 触发按钮固定高度，单位 dp（triggerAutoAdjustHeight=false 时生效） */
    public float triggerFixedHeightDp = 36f;

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

    /**
     * 选项之间的间距，单位 dp
     * ⚠️ 仅当 optionColumnCount=0（自动模式）时生效
     * 固定列数模式使用 optionFixedColumnGapDp
     */
    public int optionSpacingDp = 8;

    /**
     * 固定列数模式下，列与列之间的水平间隙，单位 dp
     * 行与行之间也使用此值作为垂直间距
     * ⚠️ 仅当 optionColumnCount > 0 时生效
     */
    public int optionFixedColumnGapDp = 8;

    // ======================== 分组标题 ========================

    /** 分组标题文字大小，单位 sp */
    public float groupTitleTextSizeSp = 15f;

    /** 分组标题文字颜色 */
    public @ColorRes int groupTitleTextColor = R.color.qf_text_primary;

    /** 分组标题底部间距（标题与选项之间的间距），单位 dp */
    public int groupTitlePaddingBottomDp = 8;

    // ======================== 关闭按钮 ========================

    /** 关闭按钮图标颜色 */
    public @ColorRes int closeIconColor = R.color.qf_close_icon;

    /** 关闭按钮尺寸，单位 dp */
    public int closeIconSizeDp = 24;

    // ======================== 弹窗 ========================

    /**
     * 弹窗高度模式：
     * ADAPTIVE = 内容少=实际高度(不可拖拽)，内容多=按 sheetExpandMode 展开
     * FIXED_PERCENT = 始终使用 peekHeightPercent
     */
    public int sheetHeightMode = SHEET_HEIGHT_ADAPTIVE;

    /** 半展开高度占屏幕百分比（默认 75%），仅 FIXED_PERCENT 或内容超出时生效 */
    public int peekHeightPercent = 75;

    /**
     * 内容多时的展开方式：
     * EXPAND_HALF = 半展开（peekHeightPercent）
     * EXPAND_FULL = 全展开（几乎占满屏幕）
     */
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
        private final QuickFilterConfig c = new QuickFilterConfig();

        // 触发按钮
        public Builder triggerText(String t) { c.triggerText = t; return this; }
        public Builder triggerTextColor(@ColorRes int r) { c.triggerTextColor = r; return this; }
        public Builder triggerTextSize(float sp) { c.triggerTextSizeSp = sp; return this; }
        public Builder triggerBgColor(@ColorRes int r) { c.triggerBgColor = r; return this; }
        public Builder triggerCornerRadius(float dp) { c.triggerCornerRadiusDp = dp; return this; }
        public Builder triggerFixedHeight(float dp) { c.triggerFixedHeightDp = dp; c.triggerAutoAdjustHeight = false; return this; }

        // 选项
        public Builder optionColumnCount(int count) { c.optionColumnCount = count; return this; }
        public Builder optionTextSize(float sp) { c.optionTextSizeSp = sp; return this; }
        public Builder optionBgSelected(@ColorRes int r) { c.optionBgSelected = r; return this; }
        public Builder optionBgNormal(@ColorRes int r) { c.optionBgNormal = r; return this; }
        public Builder optionTextColorSelected(@ColorRes int r) { c.optionTextColorSelected = r; return this; }
        public Builder optionTextColorNormal(@ColorRes int r) { c.optionTextColorNormal = r; return this; }
        public Builder optionSpacing(int dp) { c.optionSpacingDp = dp; return this; }
        public Builder optionFixedColumnGap(int dp) { c.optionFixedColumnGapDp = dp; return this; }

        // 分组标题
        public Builder groupTitleTextColor(@ColorRes int r) { c.groupTitleTextColor = r; return this; }
        public Builder groupTitlePaddingBottom(int dp) { c.groupTitlePaddingBottomDp = dp; return this; }

        // 关闭按钮
        public Builder closeIconColor(@ColorRes int r) { c.closeIconColor = r; return this; }
        public Builder closeIconSize(int dp) { c.closeIconSizeDp = dp; return this; }

        // 弹窗
        public Builder sheetHeightMode(int mode) { c.sheetHeightMode = mode; return this; }
        public Builder peekHeightPercent(int p) { c.peekHeightPercent = p; return this; }
        public Builder sheetExpandMode(int mode) { c.sheetExpandMode = mode; return this; }
        public Builder sheetTopCornerRadius(float dp) { c.sheetTopCornerRadiusDp = dp; return this; }

        public QuickFilterConfig build() { return c; }
    }
}
