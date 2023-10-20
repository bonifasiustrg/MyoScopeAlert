package com.apicta.myoscopealert.models;

import com.apicta.myoscopealert.data.repository.DiagnosesRepository;
import com.apicta.myoscopealert.ui.viewmodel.DiagnosesViewModel;

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
public final class DiagnosesViewModel_Factory implements Factory<DiagnosesViewModel> {
  private final Provider<DiagnosesRepository> repositoryProvider;

  public DiagnosesViewModel_Factory(Provider<DiagnosesRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DiagnosesViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static DiagnosesViewModel_Factory create(
      Provider<DiagnosesRepository> repositoryProvider) {
    return new DiagnosesViewModel_Factory(repositoryProvider);
  }

  public static DiagnosesViewModel newInstance(DiagnosesRepository repository) {
    return new DiagnosesViewModel(repository);
  }
}
