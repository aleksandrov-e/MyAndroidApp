package bg.mobileapps.fn2401321025.ui.list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import bg.mobileapps.fn2401321025.R;

/** Плъзгане наляво/надясно за изтриване; под картата се рисува червен фон с кошче. */
public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    public interface Listener {
        void onSwiped(int position);
    }

    private static final int CORNER_RADIUS_DP = 20;
    private static final int ICON_MARGIN_DP = 24;

    private final Listener listener;
    private final Paint background = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Drawable icon;
    private final float density;

    public SwipeToDeleteCallback(Context context, Listener listener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.listener = listener;
        density = context.getResources().getDisplayMetrics().density;
        background.setColor(MaterialColors.getColor(context,
                com.google.android.material.R.attr.colorErrorContainer, 0));
        icon = DrawableCompat.wrap(
                AppCompatResources.getDrawable(context, R.drawable.ic_delete).mutate());
        DrawableCompat.setTint(icon, MaterialColors.getColor(context,
                com.google.android.material.R.attr.colorOnErrorContainer, 0));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getBindingAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            listener.onSwiped(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
            int actionState, boolean isCurrentlyActive) {
        View item = viewHolder.itemView;
        if (dX != 0) {
            float radius = CORNER_RADIUS_DP * density;
            canvas.drawRoundRect(item.getLeft(), item.getTop(), item.getRight(),
                    item.getBottom(), radius, radius, background);

            int size = icon.getIntrinsicWidth();
            int margin = (int) (ICON_MARGIN_DP * density);
            int top = item.getTop() + (item.getHeight() - size) / 2;
            int left = dX > 0 ? item.getLeft() + margin : item.getRight() - margin - size;
            icon.setBounds(left, top, left + size, top + size);
            icon.draw(canvas);
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState,
                isCurrentlyActive);
    }
}
