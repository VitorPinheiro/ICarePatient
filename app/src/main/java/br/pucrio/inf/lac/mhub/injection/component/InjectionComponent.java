package br.pucrio.inf.lac.mhub.injection.component;

import br.pucrio.inf.lac.mhub.injection.scope.ApplicationScope;
import br.pucrio.inf.lac.mhub.services.listeners.MEPAListener;
import dagger.Component;

@ApplicationScope
@Component(
        dependencies = ApplicationComponent.class
)
public interface InjectionComponent {
     // Injects to the Components
    void inject( MEPAListener option );
}
