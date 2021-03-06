package com.permutassep.data.entity.mapper;

import com.permutassep.data.entity.UserEntity;
import com.permutassep.domain.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Mapper class used to transform {@link UserEntity} (in the data layer) to {@link User} in the
 * domain layer.
 */
@Singleton
public class UserEntityDataMapper {

    @Inject
    public UserEntityDataMapper() {
    }

    /**
     * Transform a {@link UserEntity} into an {@link User}.
     *
     * @param userEntity Object to be transformed.
     * @return {@link User} if valid {@link UserEntity} otherwise null.
     */
    public User transform(UserEntity userEntity) {
        User user = null;
        if (userEntity != null) {
            user = new User();
            user.setId(userEntity.getId());
            user.setEmail(userEntity.getEmail());
            user.setName(userEntity.getName());
            user.setPassword(userEntity.getPassword());
            user.setPhone(userEntity.getPhone());
            user.setSocialUserId(userEntity.getSocialUserId());
        }

        return user;
    }

    /**
     * Transform a List of {@link UserEntity} into a Collection of {@link User}.
     *
     * @param userEntityCollection Object Collection to be transformed.
     * @return {@link User} if valid {@link UserEntity} otherwise null.
     */
    public List<User> transform(Collection<UserEntity> userEntityCollection) {
        List<User> userList = new ArrayList<>();
        User user;
        for (UserEntity userEntity : userEntityCollection) {
            user = transform(userEntity);
            if (user != null) {
                userList.add(user);
            }
        }

        return userList;
    }

    /**
     * Transform a {@link User} into an {@link UserEntity}.
     *
     * @param user Object to be transformed.
     * @return {@link UserEntity} if valid {@link User} otherwise null.
     */
    public UserEntity transform(User user) {
        UserEntity userEntity = null;
        if (user != null) {
            userEntity = new UserEntity();
            userEntity.setId(user.getId());
            userEntity.setName(user.getName());
            userEntity.setEmail(user.getEmail());
            userEntity.setPhone(user.getPhone());
            userEntity.setPassword(user.getPassword());
            userEntity.setSocialUserId(user.getSocialUserId());
        }
        return userEntity;
    }
}
