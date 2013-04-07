package org.realityforge.tarrabah;

import javax.annotation.Nonnull;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class PipelineScopeExtension
  implements Extension
{
  @SuppressWarnings( "UnusedDeclaration" )
  public void afterBeanDiscovery( @Nonnull @Observes AfterBeanDiscovery event )
  {
    event.addContext( new PipelineContext() );
  }
}
