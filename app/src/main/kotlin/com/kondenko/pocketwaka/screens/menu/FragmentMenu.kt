package com.kondenko.pocketwaka.screens.menu


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.menu.AppRatingBottomSheetDialog
import com.kondenko.pocketwaka.screens.login.LoginActivity
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.createAdapter
import com.kondenko.pocketwaka.utils.extensions.attachToLifecycle
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.openPlayStore
import com.kondenko.pocketwaka.utils.extensions.startActivity
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.item_menu_action.view.*
import kotlinx.android.synthetic.main.item_menu_logo.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class FragmentMenu : Fragment() {

    private sealed class MenuUiModel {

        object Logo : MenuUiModel()

        data class Action(
            val iconRes: Int,
            val textRes: Int,
            val isEnabled: Boolean = true,
            val onClick: () -> Unit
        ) : MenuUiModel()

    }

    private lateinit var vm: MenuViewModel

    private val browserWindow: BrowserWindow by inject { parametersOf(context, viewLifecycleOwner) }

    private val ratingDialog = AppRatingBottomSheetDialog()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = getViewModel { parametersOf(viewLifecycleOwner) }
        val adapter = createAdapter<MenuUiModel>(view.context) {
            // TODO Disable Github if a browser Activity is not found
            items { getMenuItems(getMailActivity() != null, true) }
            viewHolder<MenuUiModel.Logo>(R.layout.item_menu_logo) { _, _ ->
                textview_menu_version.text = getString(R.string.menu_version_template, BuildConfig.VERSION_NAME)
            }
            viewHolder<MenuUiModel.Action>(R.layout.item_menu_action) { item, i ->
                isEnabled = item.isEnabled
                imageview_menu_icon.setImageResource(item.iconRes)
                textview_menu_item.setText(item.textRes)
                setOnClickListener { item.onClick() }
            }
        }
        recyclewview_menu.adapter = adapter
        ratingDialog.ratingChanges()
            .subscribe(vm::rate)
            .attachToLifecycle(viewLifecycleOwner)
        ratingDialog.sendFeedbackClicks()
            .subscribe {
                vm.sendFeedback()
                ratingDialog.dismiss()
            }
            .attachToLifecycle(viewLifecycleOwner)
        vm.state.observe(this) {
            WakaLog.d("New menu state: $it")
            when (it) {
                is MenuState.RateApp -> {
                    rateApp()
                }
                is MenuState.SendFeedback -> it.data.run {
                    sendFeedback(supportEmail, emailSubject, initialEmailText)
                }
                is MenuState.OpenGithub -> {
                    browserWindow.openUrl(it.data.githubUrl)
                }
                is MenuState.LogOut -> {
                    logout()
                }
                is MenuState.OpenPlayStore -> {
                    openPlayStore()
                }
                is MenuState.AskForFeedback -> {
                    showFeedbackButton()
                }
            }
        }
    }

    private fun getMenuItems(isFeedbackEnabled: Boolean, isGithubEnabled: Boolean = true) = listOfNotNull(
        MenuUiModel.Logo,
        MenuUiModel.Action(R.drawable.ic_menu_rate, R.string.menu_action_rate) {
            vm.rateApp()
        },
        MenuUiModel.Action(R.drawable.ic_menu_feedback, R.string.menu_action_send_feedback, isFeedbackEnabled) {
            vm.sendFeedback()
        },
        MenuUiModel.Action(R.drawable.ic_menu_github, R.string.menu_action_open_github, isGithubEnabled) {
            vm.openGithub()
        },
        MenuUiModel.Action(R.drawable.ic_menu_logout, R.string.menu_action_logout) {
            vm.logout()
        }
    )

    private fun rateApp() {
        ratingDialog.showLowRatingState(false)
        ratingDialog.show(childFragmentManager, null)
    }

    private fun openPlayStore() {
        context?.openPlayStore { playStoreUrl ->
            browserWindow.openUrl(playStoreUrl)
        }
        ratingDialog.dismiss()
    }

    private fun showFeedbackButton() {
        ratingDialog.showLowRatingState(true)
    }

    private fun sendFeedback(email: String, subject: String, initialText: String) = startActivity(
        getMailIntent().apply {
            val extras = bundleOf(
                Intent.EXTRA_EMAIL to arrayOf(email),
                Intent.EXTRA_SUBJECT to subject,
                Intent.EXTRA_TEXT to initialText
            )
            putExtras(extras)
        }
    )

    override fun onResume() {
        super.onResume()
        vm.onResume()
    }

    private fun logout() {
        requireActivity().apply {
            finish()
            startActivity<LoginActivity>()
        }
    }

    private fun getMailActivity() = context?.let {
        getMailIntent().resolveActivity(it.packageManager)
    }

    private fun getMailIntent() = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
    }

}