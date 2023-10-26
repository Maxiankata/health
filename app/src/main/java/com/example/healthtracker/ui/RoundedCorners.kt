package com.example.healthtracker.ui
import android.graphics.Outline
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi

class RoundedCorners (private val radius: Float) : ViewOutlineProvider() {
    private val bounds = Rect()

    override fun getOutline(view: View?, outline: Outline?) {
        bounds.set(0, 0, view?.width ?: 0, view?.height ?: 0)
        outline?.setRoundRect(bounds, radius)
    }
}
class ExplicableRoundedCorners(
    private val topLeftRadius: Float,
    private val topRightRadius: Float,
    private val bottomRightRadius: Float,
    private val bottomLeftRadius: Float
) : ViewOutlineProvider() {
    private val bounds = RectF()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun getOutline(view: View?, outline: Outline?) {
        bounds.set(0f, 0f, view?.width?.toFloat() ?: 0f, view?.height?.toFloat() ?: 0f)
        val path = Path()
        path.addRoundRect(
            bounds,
            floatArrayOf(
                topLeftRadius,
                topLeftRadius,
                topRightRadius,
                topRightRadius,
                bottomRightRadius,
                bottomRightRadius,
                bottomLeftRadius,
                bottomLeftRadius
            ),
            Path.Direction.CW
        )
        outline?.setPath(path)
    }
}

fun View.setExplicableRoundedCorners(
    topLeftRadius: Float,
    topRightRadius: Float,
    bottomRightRadius: Float,
    bottomLeftRadius: Float
) {
    outlineProvider = ExplicableRoundedCorners(
        topLeftRadius,
        topRightRadius,
        bottomRightRadius,
        bottomLeftRadius
    )
    clipToOutline = true
}

fun View.setRoundedCorners(radius: Float) {
    outlineProvider = RoundedCorners(radius)
    clipToOutline = true
}