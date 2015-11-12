package com.permutassep.presentation.presenter;

/**
 * By Jorge E. Hernandez (@lalongooo) 2015
 */

import android.support.annotation.NonNull;

import com.permutassep.domain.Post;
import com.permutassep.domain.exception.DefaultErrorBundle;
import com.permutassep.domain.exception.ErrorBundle;
import com.permutassep.domain.interactor.DefaultSubscriber;
import com.permutassep.domain.interactor.UseCase;
import com.permutassep.presentation.internal.di.PerActivity;
import com.permutassep.presentation.mapper.PostModelDataMapper;
import com.permutassep.presentation.view.WritePostView;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity
public class WritePostPresenter implements Presenter {

    private final UseCase writePostUseCase;
    private final PostModelDataMapper postModelDataMapper;
    private WritePostView writePostView;

    @Inject
    public WritePostPresenter(@Named("writePost") UseCase writePostUseCase, PostModelDataMapper postModelDataMapper) {
        this.writePostUseCase = writePostUseCase;
        this.postModelDataMapper = postModelDataMapper;
    }

    public void writePost() {
        this.hideViewRetry();
        this.showViewLoading();
        this.signUpUser();
    }

    private void hideViewRetry() {
        this.writePostView.hideRetry();
    }

    private void showViewLoading() {
        this.writePostView.showLoading();
    }

    private void signUpUser() {
        this.writePostUseCase.execute(new SignUpSubscriber());
    }

    private void hideViewLoading() {
        this.writePostView.hideLoading();
    }

    private void showViewRetry() {
        this.writePostView.showRetry();
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        this.writePostView.showError(errorBundle.getErrorMessage());
    }

    public void setView(@NonNull WritePostView writePostView) {
        this.writePostView = writePostView;
    }

    private void writtenPost(Post post) {
        writePostView.writtenPost(this.postModelDataMapper.transform(post));
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        writePostUseCase.unsubscribe();
    }

    private final class SignUpSubscriber extends DefaultSubscriber<Post> {
        @Override
        public void onCompleted() {
            WritePostPresenter.this.hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            WritePostPresenter.this.hideViewLoading();
            WritePostPresenter.this.showErrorMessage(new DefaultErrorBundle((Exception) e));
            WritePostPresenter.this.showViewRetry();
        }

        @Override
        public void onNext(Post post) {
            WritePostPresenter.this.writtenPost(post);
        }

    }
}
