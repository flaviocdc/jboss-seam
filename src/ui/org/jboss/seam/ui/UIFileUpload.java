package org.jboss.seam.ui;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jboss.seam.web.MultipartRequest;

/**
 * A file upload component.
 * 
 * @author Shane Bryzak
 */
public class UIFileUpload extends UIComponentBase
{
   public static final String COMPONENT_TYPE   = "org.jboss.seam.ui.UIFileUpload";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FileUpload";
      
   private String accept;
   private String required;
   private String disabled;
   private String styleClass;
   private String style;   
   
   @Override
   public void decode(FacesContext context)
   {
      super.decode(context);
      
      ServletRequest request = (ServletRequest) context.getExternalContext().getRequest();
      
      if (!(request instanceof MultipartRequest))
      {
         request = unwrapMultipartRequest(request);
      }

      if (request instanceof MultipartRequest)
      {
         MultipartRequest req = (MultipartRequest) request;
         
         String clientId = getClientId(context);         
         String contentType = req.getFileContentType(clientId);
         String fileName = req.getFileName(clientId);
         int fileSize = req.getFileSize(clientId);
                  
         ValueBinding dataBinding = getValueBinding("data");
         if (dataBinding != null)
         {
            Class cls = dataBinding.getType(context);
            if (cls.isAssignableFrom(InputStream.class))
            {
               dataBinding.setValue(context, req.getFileInputStream(clientId));
            }
            else if (cls.isAssignableFrom(byte[].class))
            {
               dataBinding.setValue(context, req.getFileBytes(clientId));
            }
         }
         
         ValueBinding vb = getValueBinding("contentType");
         if (vb != null)
            vb.setValue(context, contentType);
         
         vb = getValueBinding("fileName");
         if (vb != null)
            vb.setValue(context, fileName);
         
         vb = getValueBinding("fileSize");
         if (vb != null)
            vb.setValue(context, fileSize);
      }      
   }
      
   private ServletRequest unwrapMultipartRequest(ServletRequest request)
   {      
      while (!(request instanceof MultipartRequest))
      {
         boolean found = false;
         
         for (Method m : request.getClass().getMethods())
         {
            if (ServletRequest.class.isAssignableFrom(m.getReturnType()) && 
                m.getParameterTypes().length == 0)
            {
               try
               {
                  request = (ServletRequest) m.invoke(request);
                  found = true;
                  break;
               }
               catch (Exception ex) { /* Ignore, try the next one */ } 
            }
         }
         
         if (!found)
         {
            for (Field f : request.getClass().getDeclaredFields())
            {
               if (ServletRequest.class.isAssignableFrom(f.getType()))
               {
                  try
                  {
                     request = (ServletRequest) f.get(request);
                  }
                  catch (Exception ex) { /* Ignore */}
               }
            }
         }
         
         if (!found) break;
      }
      
      return request;     
   }
      
   @Override
   public void encodeEnd(FacesContext context) 
      throws IOException
   {
      super.encodeEnd(context);

      ResponseWriter writer = context.getResponseWriter();
      writer.startElement(HTML.INPUT_ELEM, this);      
      writer.writeAttribute(HTML.TYPE_ATTR, HTML.FILE_ATTR, null);
      
      String clientId = this.getClientId(context);      
      writer.writeAttribute(HTML.ID_ATTR, clientId, null);     
      writer.writeAttribute(HTML.NAME_ATTR, clientId, null);
      
      ValueBinding vb = getValueBinding("accept");
      if (vb != null)
      {
         writer.writeAttribute(HTML.ACCEPT_ATTR, vb.getValue(context), null);
      }
      else if (accept != null)
      {
         writer.writeAttribute(HTML.ACCEPT_ATTR, accept, null);
      }
      
      writer.endElement(HTML.INPUT_ELEM);
   }
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   public String getAccept()
   {
      return accept;
   }

   public void setAccept(String accept)
   {
      this.accept = accept;
   }

   public String getDisabled()
   {
      return disabled;
   }

   public void setDisabled(String disabled)
   {
      this.disabled = disabled;
   }

   public String getRequired()
   {
      return required;
   }

   public void setRequired(String required)
   {
      this.required = required;
   }

   public String getStyle()
   {
      return style;
   }

   public void setStyle(String style)
   {
      this.style = style;
   }

   public String getStyleClass()
   {
      return styleClass;
   }

   public void setStyleClass(String styleClass)
   {
      this.styleClass = styleClass;
   }

}
