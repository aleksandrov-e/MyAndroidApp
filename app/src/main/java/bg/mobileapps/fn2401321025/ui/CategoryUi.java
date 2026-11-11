package bg.mobileapps.fn2401321025.ui;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import bg.mobileapps.fn2401321025.R;
import bg.mobileapps.fn2401321025.domain.TaskCategory;

/** Как изглежда всяка категория на екрана: име, цвят и икона. */
public final class CategoryUi {

    private CategoryUi() {
    }

    @StringRes
    public static int labelRes(TaskCategory category) {
        switch (category) {
            case STUDY:
                return R.string.category_study;
            case WORK:
                return R.string.category_work;
            case SHOPPING:
                return R.string.category_shopping;
            default:
                return R.string.category_personal;
        }
    }

    @ColorRes
    public static int colorRes(TaskCategory category) {
        switch (category) {
            case STUDY:
                return R.color.category_study;
            case WORK:
                return R.color.category_work;
            case SHOPPING:
                return R.color.category_shopping;
            default:
                return R.color.category_personal;
        }
    }

    @DrawableRes
    public static int iconRes(TaskCategory category) {
        switch (category) {
            case STUDY:
                return R.drawable.ic_category_study;
            case WORK:
                return R.drawable.ic_category_work;
            case SHOPPING:
                return R.drawable.ic_category_shopping;
            default:
                return R.drawable.ic_category_personal;
        }
    }
}
