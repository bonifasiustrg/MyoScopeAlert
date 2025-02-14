package com.apicta.myoscopealert.di;

import com.apicta.myoscopealert.data.repository.DiagnosesRepository;
import com.apicta.myoscopealert.network.MLApi;
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
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AppModule_ProvideDiagnosesRepositoryFactory implements Factory<DiagnosesRepository> {
  private final Provider<UserApi> userApiProvider;

  private final Provider<MLApi> mlApiProvider;

  public AppModule_ProvideDiagnosesRepositoryFactory(Provider<UserApi> userApiProvider,
      Provider<MLApi> mlApiProvider) {
    this.userApiProvider = userApiProvider;
    this.mlApiProvider = mlApiProvider;
  }

  @Override
  public DiagnosesRepository get() {
    return provideDiagnosesRepository(userApiProvider.get(), mlApiProvider.get());
  }

  public static AppModule_ProvideDiagnosesRepositoryFactory create(
      Provider<UserApi> userApiProvider, Provider<MLApi> mlApiProvider) {
    return new AppModule_ProvideDiagnosesRepositoryFactory(userApiProvider, mlApiProvider);
  }

  public static DiagnosesRepository provideDiagnosesRepository(UserApi userApi, MLApi mlApi) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDiagnosesRepository(userApi, mlApi));
  }
}
