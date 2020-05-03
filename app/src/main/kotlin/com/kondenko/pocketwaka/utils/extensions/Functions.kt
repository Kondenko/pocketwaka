package com.kondenko.pocketwaka.utils.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Path
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.Const
import java.util.*

fun notNull(vararg values: Any?): Boolean = values.all { it != null }
fun createPath(build: Path.() -> Unit): Path = Path().apply {
    build()
    close()
}

fun <T> forEachNonNull(vararg items: T, action: (T) -> Unit) = items.forEach(action)

fun <T> forEach(vararg items: T?, action: (T?) -> Unit) = items.forEach(action)

fun date(day: Int, month: Int, year: Int): Long = Calendar.getInstance().run {
    set(Calendar.DAY_OF_MONTH, day)
    set(Calendar.MONTH, month)
    set(Calendar.YEAR, year)
    time.time
}

fun ifDebug(action: () -> Unit) =
      if (BuildConfig.DEBUG) {
          action()
          true
      } else {
          false
      }

fun apiAtLeast(version: Int, action: (() -> Unit)? = null): Boolean {
    if (Build.VERSION.SDK_INT >= version) {
        action?.invoke()
        return true
    }
    return false
}

fun createColorAnimator(
      context: Context?,
      @ColorRes initialColorRes: Int,
      @ColorRes finalColorRes: Int,
      onUpdate: (color: Int) -> Unit
): ValueAnimator? = context?.run {
    createColorAnimator(getColorCompat(initialColorRes), getColorCompat(finalColorRes), onUpdate)
}

@Suppress("UsePropertyAccessSyntax")
fun createColorAnimator(
      @ColorInt initialColor: Int,
      @ColorInt finalColor: Int,
      onUpdate: (color: Int) -> Unit
): ValueAnimator =
      ValueAnimator.ofInt(initialColor, finalColor).apply {
          setDuration(Const.DEFAULT_ANIM_DURATION)
          setEvaluator(ArgbEvaluator())
          addUpdateListener {
              onUpdate(animatedValue as Int)
          }
      }