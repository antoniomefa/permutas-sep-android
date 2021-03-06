package com.permutassep.presentation.internal.di.components;

import android.content.Context;

import com.permutassep.domain.executor.PostExecutionThread;
import com.permutassep.domain.executor.ThreadExecutor;
import com.permutassep.domain.repository.AuthenticationRepository;
import com.permutassep.domain.repository.PasswordResetRepository;
import com.permutassep.domain.repository.PostRepository;
import com.permutassep.presentation.internal.di.modules.ApplicationModule;
import com.permutassep.presentation.navigation.Navigator;
import com.permutassep.ui.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);


    //Exposed to sub-graphs.
    Context context();

    Navigator navigator();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    PostRepository postRepository();

    AuthenticationRepository authenticationRepository();

    PasswordResetRepository passwordResetRepository();
}
