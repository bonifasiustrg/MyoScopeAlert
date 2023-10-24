package com.apicta.myoscopealert.di;

import com.apicta.myoscopealert.data.repository.DiagnosesRepository;
import com.apicta.myoscopealert.network.UserApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideDiagnosesRepositoryFactory implements Factory<DiagnosesRepository> {
  private final Provider<UserApi> userApiProvider;

  public AppModule_ProvideDiagnosesRepositoryFactory(Provider<UserApi> userApiProvider) {
    this.userApiProvider = userApiProvider;
  }

  @Override
  public DiagnosesRepository get() {
    return provideDiagnosesRepository(userApiProvider.get());
  }

  public static AppModule_ProvideDiagnosesRepositoryFactory create(
      Provider<UserApi> userApiProvider) {
    return new AppModule_ProvideDiagnosesRepositoryFactory(userApiProvider);
  }

  public static DiagnosesRepository provideDiagnosesRepository(UserApi userApi) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDiagnosesRepository(userApi));
  }
}
