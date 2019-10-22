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
import com.kondenko.pocketwaka.alphaDisabledView
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.analytics.Screen
import com.kondenko.pocketwaka.analytics.ScreenTracker
import com.kondenko.pocketwaka.domain.menu.AppRatingBottomSheetDialog
import com.kondenko.pocketwaka.screens.login.LoginActivity
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.createAdapter
import com.kondenko.pocketwaka.utils.extensions.*
import kotlinx.android.synthetic.main.dialog_app_rating.*
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.item_menu_action.view.*
import kotlinx.android.synthetic.main.item_menu_logo.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
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

    private val vm: MenuViewModel by viewModel()

    private val screenTracker: ScreenTracker by inject()

    private val eventTracker: EventTracker by inject()

    private val browserWindow: BrowserWindow by inject { parametersOf(context, viewLifecycleOwner) }

    private lateinit var ratingDialog: AppRatingBottomSheetDialog
    private val tagRatingDialog = "ratingDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ratingDialog =
              childFragmentManager.findFragmentByTag(tagRatingDialog) as AppRatingBottomSheetDialog?
                    ?: AppRatingBottomSheetDialog()
        val adapter = createAdapter<MenuUiModel>(view.context) {
            items { getMenuItems(getMailActivity() != null, true) }
            viewHolder<MenuUiModel.Logo>(R.layout.item_menu_logo) { _, _ ->
                textview_menu_version.text = getString(R.string.menu_version_template, BuildConfig.VERSION_NAME)
            }
            viewHolder<MenuUiModel.Action>(R.layout.item_menu_action) { item, i ->
                isEnabled = item.isEnabled
                imageview_menu_icon.setImageResource(item.iconRes)
                textview_menu_item.setText(item.textRes)
                setOnClickListener { item.onClick() }
                alpha = if (isEnabled) 1f else alphaDisabledView
            }
        }
        recyclewview_menu.adapter = adapter
        with(ratingDialog) {
            ratingChanges()
                  .doOnNext { eventTracker.log(Event.Menu.RatingGiven(it)) }
                  .subscribe(vm::rate)
                  .attachToLifecycle(this@FragmentMenu.viewLifecycleOwner)
            sendFeedbackClicks()
                  .subscribe {
                      eventTracker.log(Event.Menu.FeedbackButtonClicked(isFromRating = true))
                      vm.sendFeedback()
                      dismiss()
                  }
                  .attachToLifecycle(this@FragmentMenu.viewLifecycleOwner)
        }
        vm.state().observe(this) {
            WakaLog.d("New menu state: $it")
            if (it !is MenuState.ShowRatingDialog) {
                ratingDialog.safeDismiss()
            }
            when (it) {
                is MenuState.ShowRatingDialog -> {
                    rateApp()
                    showFeedbackButton(it.askForFeedback, it.data?.supportEmail)
                    if (it.openPlayStore) openPlayStore()
                    ratingDialog.onDismiss = {
                        ratingDialog.ratingbar_rating_dialog?.rating = 0
                        showFeedbackButton(false, null)
                        vm.onDialogDismissed()
                    }
                }
                is MenuState.SendFeedback -> it.data.run {
                    sendFeedback(supportEmail, emailSubject, initialEmailText)
                }
                is MenuState.OpenGithub -> {
                    eventTracker.log(Event.Menu.GithubClicked)
                    browserWindow.openUrl(it.data.githubUrl)
                }
                is MenuState.LogOut -> {
                    logout()
                }
            }
        }
    }

    private fun getMenuItems(isMailAvailable: Boolean, isGithubEnabled: Boolean = true) = listOfNotNull(
          MenuUiModel.Logo,
          MenuUiModel.Action(R.drawable.ic_menu_rate, R.string.menu_action_rate) {
              eventTracker.log(Event.Menu.RatingButtonClicked)
              vm.rateApp()
          },
          MenuUiModel.Action(R.drawable.ic_menu_feedback, R.string.menu_action_send_feedback, isMailAvailable) {
              eventTracker.log(Event.Menu.FeedbackButtonClicked(isFromRating = false))
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
        if (!ratingDialog.isShown) ratingDialog.show(childFragmentManager, tagRatingDialog)
    }

    private fun showFeedbackButton(show: Boolean, supportEmail: String?) {
        ratingDialog.showLowRatingState(show = show, isMailAvailable = getMailActivity() != null, supportEmail = supportEmail)
    }

    private fun openPlayStore() {
        eventTracker.log(Event.Menu.PlayStoreOpened)
        context?.openPlayStore { playStoreUrl ->
            browserWindow.openUrl(playStoreUrl)
        }
        ratingDialog.dismiss()
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
        screenTracker.log(activity, Screen.Menu)
        vm.onResume()
    }

    private fun logout() {
        requireActivity().apply {
            eventTracker.log(Event.Menu.Logout)
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