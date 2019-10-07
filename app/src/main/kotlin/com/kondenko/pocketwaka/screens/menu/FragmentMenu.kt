package com.kondenko.pocketwaka.screens.menu


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.login.LoginActivity
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.createAdapter
import com.kondenko.pocketwaka.utils.extensions.observe
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
        data class Action(val iconRes: Int, val textRes: Int, val onClick: () -> Unit) : MenuUiModel()
    }

    private lateinit var vm: MenuViewModel

    private val browserWindow: BrowserWindow by inject { parametersOf(context, viewLifecycleOwner) }

    private val menuItems = listOf(
            MenuUiModel.Logo,
            MenuUiModel.Action(R.drawable.ic_menu_rate, R.string.menu_action_rate) {
                Toast.makeText(context, "Rating", Toast.LENGTH_SHORT).show()
            },
            MenuUiModel.Action(R.drawable.ic_menu_feedback, R.string.menu_action_send_feedback) {
                Toast.makeText(context, "Feedback", Toast.LENGTH_SHORT).show()
            },
            MenuUiModel.Action(R.drawable.ic_menu_github, R.string.menu_action_open_github) {
                vm.openGithub()
            },
            MenuUiModel.Action(R.drawable.ic_menu_logout, R.string.menu_action_logout) {
                vm.logout()
            }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = getViewModel { parametersOf(viewLifecycleOwner) }
        vm.state.observe(this) {
            WakaLog.d("New menu state: $it")
            when (it) {
                is MenuState.OpenGithub -> browserWindow.openUrl(it.data!!.githubUrl)
                is MenuState.LogOut -> logout()
            }
        }
        recyclewview_menu.adapter = createAdapter<MenuUiModel>(view.context) {
            items { menuItems }
            viewHolder<MenuUiModel.Logo>(R.layout.item_menu_logo) { _, _ ->
                textview_menu_version.text = getString(R.string.menu_version_template, BuildConfig.VERSION_NAME)
            }
            viewHolder<MenuUiModel.Action>(R.layout.item_menu_action) { item, i ->
                imageview_menu_icon.setImageResource(item.iconRes)
                textview_menu_item.setText(item.textRes)
                setOnClickListener { item.onClick() }
            }
        }
    }

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

}
