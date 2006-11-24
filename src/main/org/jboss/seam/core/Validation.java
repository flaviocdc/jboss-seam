package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Name("org.jboss.seam.core.validation")
@Intercept(InterceptionType.NEVER)
@Scope(ScopeType.APPLICATION)
@Install(precedence=BUILT_IN)
public class Validation
{

   private Map<Class, ClassValidator> classValidators = Collections.synchronizedMap( new HashMap<Class, ClassValidator>() ); 
   
   public ClassValidator getValidator(Class modelClass, String name)
   {
      ClassValidator result = classValidators.get(modelClass);
      if (result==null)
      {
         result = createValidator(modelClass, name);
         classValidators.put(modelClass, result);
      }
      return result;
   }
   
   public ClassValidator createValidator(Class modelClass, String name)
   {
      Component component = name==null ? null : Component.forName(name);
      if (component==null)
      {
         java.util.ResourceBundle bundle = ResourceBundle.instance();
         return bundle==null ? 
               new ClassValidator(modelClass) : 
               new ClassValidator(modelClass, bundle);
      }
      else
      {
         return component.getValidator();
      }
   }

   public InvalidValue[] validate(FacesContext context, String propertyExpression, Object value)
   {
      int sep = propertyExpression.lastIndexOf('.');
      if (sep<=0) 
      {
         throw new RuntimeException("not an attribute value binding: " + propertyExpression);
      }
      String componentName = propertyExpression.substring(2, sep);
      String modelExpression = propertyExpression.substring(0, sep) + '}';
      String propertyName = propertyExpression.substring( modelExpression.length(), propertyExpression.length()-1 );
   
      Object model = context.getApplication().createValueBinding(modelExpression).getValue(context);
      ClassValidator validator = getValidator( model.getClass(), componentName );    
      return validator.getPotentialInvalidValues(propertyName, value);
   }

   public static Validation instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (Validation) Component.getInstance(Validation.class, ScopeType.APPLICATION);
   }

}
