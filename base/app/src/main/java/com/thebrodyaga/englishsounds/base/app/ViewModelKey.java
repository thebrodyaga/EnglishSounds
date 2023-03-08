package com.thebrodyaga.englishsounds.base.app;

import androidx.lifecycle.ViewModel;
import dagger.MapKey;

@MapKey
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}
