package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.core.Init.ObserverMethod;
import org.jboss.seam.core.Init.ObserverMethodBinding;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Resources;

@Scope(ScopeType.STATELESS)
@Intercept(NEVER)
@Name("org.jboss.seam.core.events")
@Install(precedence=BUILT_IN)
public class Events 
{
   
   private static final LogProvider log = Logging.getLogProvider(Events.class);
   
   @Create
   public void initialize() throws DocumentException
   {
      InputStream stream = Resources.getResourceAsStream("/WEB-INF/events.xml");      
      if (stream==null)
      {
         log.info("no events.xml file found");
      }
      else
      {
         log.info("reading events.xml");
      }
   }
   
   public void addListener(String type, String methodBindingExpression)
   {
      MethodBinding methodBinding = Expressions.instance().createMethodBinding(methodBindingExpression);
      Init.instance().addObserverMethodBinding(type, methodBinding);
   }
   
   public void raiseEvent(String type, Object... parameters)
   {
      log.debug("Processing event:" + type);
      List<Init.ObserverMethodBinding> list = Init.instance().getObserverMethodBindings(type);
      if (list!=null)
      {
         for (ObserverMethodBinding listener: list )
         {
            listener.getMethodBinding().invoke(parameters);
         }
      }
      List<Init.ObserverMethod> observers = Init.instance().getObserverMethods(type);
      if (observers!=null)
      {
         for (ObserverMethod observer: observers)
         {
            Object listener = Component.getInstance( observer.getComponent().getName(), observer.isCreate() );
            if (listener!=null)
            {
               observer.getComponent().callComponentMethod(listener, observer.getMethod(), parameters);
            }
         }
      }
   }
   
   public void raiseAsynchronousEvent(String type, Object... parameters)
   {
      Dispatcher.instance().scheduleEvent(type, 0l, null, null, parameters);
   }
   
   public void raiseTransactionSuccessEvent(String type, Object... parameters)
   {
      LocalTransactionListener transactionListener = TransactionListener.instance();
      if (transactionListener==null)
      {
         throw new IllegalStateException("org.jboss.seam.core.transactionListener is not installed");
      }
      transactionListener.scheduleEvent(type, parameters);
   }
   
   public void raiseTimedEvent(String type, long duration, Object... parameters)
   {
      Dispatcher.instance().scheduleEvent(type, duration, null, null, parameters);
   }
   
   public void raiseTimedEvent(String type, Date expiration, Object... parameters)
   {
      Dispatcher.instance().scheduleEvent(type, null, expiration, null, parameters);
   }
   
   public void raiseTimedEvent(String type, Date expiration, long intervalDuration, Object... parameters)
   {
      Dispatcher.instance().scheduleEvent(type, null, expiration, intervalDuration, parameters);
   }
   
   public void raiseTimedEvent(String type, long duration, long intervalDuration)
   {
      Dispatcher.instance().scheduleEvent(type, duration, null, intervalDuration);
   }
   
   public static boolean exists()
   {
      return Contexts.isApplicationContextActive() && instance()!=null;
   }

   public static Events instance()
   {
      return (Events) Component.getInstance(Events.class, ScopeType.STATELESS);
   }
   
}
