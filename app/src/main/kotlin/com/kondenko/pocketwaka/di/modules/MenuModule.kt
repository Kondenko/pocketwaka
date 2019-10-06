package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.screens.menu.MenuViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val menuModule = module {
    viewModel { MenuViewModel(get()) }
}