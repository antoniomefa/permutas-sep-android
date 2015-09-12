package com.permutassep.presentation.presenter;

import com.permutassep.domain.Post;
import com.permutassep.domain.exception.DefaultErrorBundle;
import com.permutassep.domain.exception.ErrorBundle;
import com.permutassep.domain.interactor.DefaultSubscriber;
import com.permutassep.domain.interactor.UseCase;
import com.permutassep.presentation.exception.ErrorMessageFactory;
import com.permutassep.presentation.mapper.PostModelDataMapper;
import com.permutassep.presentation.model.PostModel;
import com.permutassep.presentation.view.PostsListView;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * By Jorge E. Hernandez (@lalongooo) 2015
 */

public class PostsListPresenter implements Presenter {

    private final UseCase getPostByUserUseCase;
    private final PostModelDataMapper postModelDataMapper;
    private PostsListView postsListView;

    @Inject
    public PostsListPresenter(@Named("postByUser") UseCase getPostsList, PostModelDataMapper postModelDataMapper) {
        this.getPostByUserUseCase = getPostsList;
        this.postModelDataMapper = postModelDataMapper;
    }

    public void setView(PostsListView postsListView) {
        this.postsListView = postsListView;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        getPostByUserUseCase.unsubscribe();
    }

    /**
     * Initializes the presenter by start retrieving the user list.
     */
    public void initialize() {
        this.loadPostList();
    }

    /**
     * Loads all users.
     */
    private void loadPostList() {
        this.hideViewRetry();
        this.showViewLoading();
        this.getUserList();
    }

    private void hideViewRetry() {
        this.postsListView.hideRetry();
    }

    private void showViewLoading() {
        this.postsListView.showLoading();
    }

    private void getUserList() {
        this.getPostByUserUseCase.execute(new UserListSubscriber());
    }

    private final class UserListSubscriber extends DefaultSubscriber<List<Post>> {

        @Override
        public void onCompleted() {
            PostsListPresenter.this.hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            PostsListPresenter.this.hideViewLoading();
            PostsListPresenter.this.showErrorMessage(new DefaultErrorBundle((Exception) e));
            PostsListPresenter.this.showViewRetry();
        }

        @Override
        public void onNext(List<Post> users) {
            PostsListPresenter.this.showUsersCollectionInView(users);
        }
    }

    private void hideViewLoading() {
        this.postsListView.hideLoading();
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.postsListView.getContext(), errorBundle.getException());
        this.postsListView.showError(errorMessage);
    }

    private void showViewRetry() {
        this.postsListView.showRetry();
    }

    private void showUsersCollectionInView(Collection<Post> postCollection) {
        final Collection<PostModel> postModelCollection = this.postModelDataMapper.transform(postCollection);
        this.postsListView.renderPostList(postModelCollection);
    }

}