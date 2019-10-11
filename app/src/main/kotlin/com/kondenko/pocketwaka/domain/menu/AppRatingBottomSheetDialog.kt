package com.kondenko.pocketwaka.domain.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kondenko.pocketwaka.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_app_rating.*

class AppRatingBottomSheetDialog : BottomSheetDialogFragment() {

    private val ratingChanges = PublishSubject.create<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_app_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ratingbar_rating_dialog.ratingChanges().subscribeWith(ratingChanges)
    }

    fun ratingChanges() = ratingChanges

}