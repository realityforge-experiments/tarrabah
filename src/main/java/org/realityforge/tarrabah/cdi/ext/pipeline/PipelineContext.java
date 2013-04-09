package org.realityforge.tarrabah.cdi.ext.pipeline;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

public class PipelineContext
  implements Context
{
  private final Map<Pipeline, PipelineEntry> _pipelines = new HashMap<Pipeline, PipelineEntry>();

  public Class<? extends Annotation> getScope()
  {
    return PipelineScoped.class;
  }

  public <T> T get( final Contextual<T> contextual,
                    final CreationalContext<T> creationalContext )
  {
    if ( !isActive() )
    {
      throw new ContextNotActiveException();
    }
    System.out.println( "PipelineContext.get" );
    final Bean<T> bean = (Bean<T>) contextual;
    final Map<String, BeanEntry<T>> store = getStore();
    final String id = getId( bean );
    if ( store.containsKey( id ) )
    {
      return store.get( id ).getInstance();
    }
    else
    {
      final T t = bean.create( creationalContext );
      store.put( id, new BeanEntry<T>( contextual, creationalContext, t ) );
      return t;
    }
  }

  private <T> String getId( final Bean<T> bean )
  {
    return bean.toString();
    //return bean.getName();
  }

  public <T> T get( final Contextual<T> contextual )
  {
    if ( !isActive() )
    {
      throw new ContextNotActiveException();
    }
    System.out.println( "PipelineContext.get" );
    final Bean<T> bean = (Bean<T>) contextual;
    final Map<String, BeanEntry<T>> store = getStore();
    final String id = getId( bean );
    if ( store.containsKey( id ) )
    {
      return store.get( id ).getInstance();
    }
    else
    {
      return null;
    }
  }

  public boolean isActive()
  {
    System.out.println( "PipelineContext.isActive" );
    return null != Pipeline.current();
  }

  final void unloadPipeline( final Pipeline pipeline )
  {
    PipelineEntry entry = _pipelines.remove( pipeline );
    if ( null != entry )
    {
      final Map<String, BeanEntry<Object>> entryMap = entry.getStore();

      for ( final BeanEntry<Object> beanEntry : entryMap.values() )
      {
        beanEntry.getContextual().destroy( beanEntry.getInstance(), beanEntry.getCreationalContext() );
      }

      entryMap.clear();
    }
  }

  @SuppressWarnings( "unchecked" )
  private <T> Map<String, BeanEntry<T>> getStore()
  {
    final Pipeline pipeline = Pipeline.current();
    if ( null != pipeline )
    {
      PipelineEntry entry = _pipelines.get( pipeline );
      if ( null == entry )
      {
        entry = new PipelineEntry( pipeline, this );
        _pipelines.put( pipeline, entry );
      }
      return entry.getStore();
    }
    else
    {
      return Collections.emptyMap();
    }
  }
}