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
import io.reactivex.exceptions.CompositeException
import retrofit2.HttpException
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

val isDebug get() = BuildConfig.DEBUG

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
      duration: Long = Const.DEFAULT_ANIM_DURATION,
      onUpdate: (color: Int) -> Unit
): ValueAnimator? = context?.run {
    createColorAnimator(color(initialColorRes), color(finalColorRes), duration, onUpdate)
}

@Suppress("UsePropertyAccessSyntax")
fun createColorAnimator(
      @ColorInt initialColor: Int,
      @ColorInt finalColor: Int,
      duration: Long = Const.DEFAULT_ANIM_DURATION,
      onUpdate: (color: Int) -> Unit
): ValueAnimator =
      ValueAnimator.ofInt(initialColor, finalColor).apply {
          setDuration(duration)
          setEvaluator(ArgbEvaluator())
          addUpdateListener {
              onUpdate(animatedValue as Int)
          }
      }

fun Throwable?.asHttpException(): HttpException? =
      (this as? CompositeException)
            ?.exceptions
            ?.findInstance<HttpException>()
            ?: this as? HttpException