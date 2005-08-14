package org.jboss.seam.example.registration;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.ScopeType;

@Entity
@Name("user")
@Scope(ScopeType.CONVERSATION)
public class User implements Serializable
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;

   private int id;

   private String username;

   private String password;

   private int age;

   public User()
   {
   }

   @Id(generate = GeneratorType.AUTO)
   public int getId()
   {
      return id;
   }

   void setId(int id)
   {
      this.id = id;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public int getAge()
   {
      return age;
   }

   public void setAge(int age)
   {
      this.age = age;
   }

}
