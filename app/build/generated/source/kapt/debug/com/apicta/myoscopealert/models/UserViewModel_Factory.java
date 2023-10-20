package com.apicta.myoscopealert.models;

import com.apicta.myoscopealert.data.DataStoreManager;
import com.apicta.myoscopealert.data.repository.UserRepository;
import com.apicta.myoscopealert.ui.viewmodel.UserViewModel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class UserViewModel_Factory implements Factory<UserViewModel> {
  private final Provider<UserRepository> repositoryProvider;

  private final Provider<DataStoreManager> dataStoreManagerProvider;

  public UserViewModel_Factory(Provider<UserRepository> repositoryProvider,
      Provider<DataStoreManager> dataStoreManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.dataStoreManagerProvider = dataStoreManagerProvider;
  }

  @Override
  public UserViewModel get() {
    return newInstance(repositoryProvider.get(), dataStoreManagerProvider.get());
  }

  public static UserViewModel_Factory create(Provider<UserRepository> repositoryProvider,
      Provider<DataStoreManager> dataStoreManagerProvider) {
    return new UserViewModel_Factory(repositoryProvider, dataStoreManagerProvider);
  }

  public static UserViewModel newInstance(UserRepository repository,
      DataStoreManager dataStoreManager) {
    return new UserViewModel(repository, dataStoreManager);
  }
}
