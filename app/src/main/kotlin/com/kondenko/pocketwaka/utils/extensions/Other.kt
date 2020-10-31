package com.kondenko.pocketwaka.utils.extensions

import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.Matrix
import android.graphics.Path
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.crashlytics.android.Crashlytics
import com.kondenko.pocketwaka.BuildConfig
import io.reactivex.Single
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Float.negateIfTrue(condition: Boolean) = if (condition) -this else this

fun <T> T?.singleOrErrorIfNull(exception: Throwable = NullPointerException("Couldn't convert a null object to a Single")): Single<T> = this?.let { Single.just(it) }
      ?: Single.error(exception)

inline fun FragmentManager.transaction(crossinline action: androidx.fragment.app.FragmentTransaction.() -> Unit) {
    this.beginTransaction().apply(action).commit()
}

/**
 * Prints log output and sends a report to Crashlytics about the given exception
 */
fun Throwable.report(message: String? = null, printLog: Boolean = true) {
    if (printLog) {
        Timber.e(this, message ?: this.message)
    }
    @Suppress("ConstantConditionIf")
    if (!BuildConfig.DEBUG) {
        Crashlytics.logException(this)
    }
}

fun Path.applyMatrix(actions: Matrix.() -> Unit) = Matrix().also { matrix ->
    matrix.actions()
    transform(matrix)
}

operator fun <T : Comparable<T>> ClosedRange<T>.component1() = this.start

operator fun <T : Comparable<T>> ClosedRange<T>.component2() = this.endInclusive

operator fun <T> List<T>.times(times: Int): List<T> {
    val list = this.toMutableList()
    for (i in (1 until times)) {
        list.addAll(this)
    }
    return list
}

fun SharedPreferences.getStringOrThrow(key: String) =
      getString(key, null) ?: throw NullPointerException("Preference with key $key not found")

fun <T> SharedPreferences.getOrNull(key: String, getter: SharedPreferences.(String) -> T): T? =
      if (!contains(key)) null else getter(key)

fun <T> T?.toListOrEmpty() = this?.let { listOf(it) } ?: emptyList()

operator fun <T> List<T>.get(range: IntRange): List<T> {
    assert(range.last < size) { "The last element of range should be less than the size of the list (${range.last} was larger than $size)" }
    return range.map { index -> elementAt(index) }
}

fun LottieAnimationView.setFillTint(color: Int) =
      addValueCallback(KeyPath("**"), LottieProperty.COLOR, LottieValueCallback(color))

fun LottieAnimationView.setStrokeTint(color: Int) =
      addValueCallback(KeyPath("**"), LottieProperty.STROKE_COLOR, LottieValueCallback(color))

fun LottieAnimationView.playAnimation(duration: Long, interpolator: Interpolator = LinearInterpolator(), reverse: Boolean = false) {
    val values = if (reverse) floatArrayOf(1f, 0f) else floatArrayOf(0f, 1f)
    ValueAnimator.ofFloat(*values).apply {
        setDuration(duration)
        setInterpolator(interpolator)
        addUpdateListener {
            progress = animatedValue as Float
        }
    }.start()
}

inline fun <reified T : Any> T.className(includeSuperclass: Boolean = false, separator: String = "_"): String {
    val className = this::class.java.simpleName
    val superclassName = this::class.java.superclass?.simpleName
    return if (!includeSuperclass || superclassName == null) className else "$superclassName$separator$className"
}

val DialogFragment.isShown
    get() = dialog?.isShowing == true && !isRemoving

fun DialogFragment.safeDismiss() {
    if (isShown) dismiss()
}

fun Long.roundDateToDay() = Calendar.getInstance().run {
    time = Date(this@roundDateToDay)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    time.time
}

fun Long.isSameDay(other: Long) = other.roundDateToDay() == this.roundDateToDay()

fun Number.secondsToHoursAndMinutes(): Pair<Long, Long> {
    val hours = TimeUnit.SECONDS.toHours(this.toLong())
    val minutes = TimeUnit.SECONDS.toMinutes(this.toLong()) - TimeUnit.HOURS.toMinutes(hours)
    return hours to minutes
}

infix fun LocalDate.dailyRangeTo(other: LocalDate): List<LocalDate> {
    if (this.isAfter(other)) throw IllegalArgumentException("Start date comes after end ∂ate")
    val list = mutableListOf<LocalDate>()
    var nextDate: LocalDate = this
    while (nextDate <= other) {
        list.add(nextDate)
        nextDate = nextDate.plusDays(1)
    }
    return list
}

fun getMonthYearFormat(year: Int, currentYear: Int): SimpleDateFormat {
    val patternCurrentYear = "LLLL"
    val patternOtherYear = "LLLL yyyy"
    val pattern = if (year == currentYear) patternCurrentYear else patternOtherYear
    return SimpleDateFormat(pattern, Locale.getDefault())
}

inline fun <reified R> Iterable<*>.findInstance(): R? = find { it is R } as R

fun <T, R> List<T>.appendOrReplace(other: List<T>, keySelector: (T) -> R): List<T> =
      (other.reversed() + this.reversed())
            .distinctBy { keySelector(it) }
            .reversed()

fun <T> Array<T>.onEach(action: (T) -> Unit): Array<T> = apply { forEach(action) }