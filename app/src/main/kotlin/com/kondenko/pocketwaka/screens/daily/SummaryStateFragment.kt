package com.kondenko.pocketwaka.screens.daily

import androidx.core.view.isVisible
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.StateFragment
import kotlinx.android.synthetic.main.fragment_state.*

class SummaryStateFragment : StateFragment() {

    override fun setState(state: State<*>, onActionClick: (() -> Unit)?) {
        if (state is SummaryState.EmptyRange) {
            setContent(R.drawable.img_state_empty_today, R.string.summary_projects_state_empty_today_title)
            button_state_action_retry.isVisible = false
            button_state_action_open_plugins.isVisible = false
        } else {
            super.setState(state, onActionClick)
        }
    }

}