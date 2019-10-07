package com.kondenko.pocketwaka.di.modules

import androidx.lifecycle.LifecycleOwner
import com.kondenko.pocketwaka.data.menu.MenuRepository
import com.kondenko.pocketwaka.domain.menu.GetMenuUiModel
import com.kondenko.pocketwaka.screens.menu.MenuViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val menuModule = module {
    single { MenuRepository(get(), get()) }
    single { GetMenuUiModel(get(), get()) }
    viewModel { (lifecycleOwner: LifecycleOwner) -> MenuViewModel(lifecycleOwner, get(), get()) }
}