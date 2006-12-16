package org.jboss.seam.util;

import javax.transaction.UserTransaction;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public abstract class Work<T>
{
   private static final LogProvider log = Logging.getLogProvider(Work.class);
   
   protected abstract T work() throws Exception;
   
   protected boolean isNewTransactionRequired(boolean transactionActive)
   {
      return !transactionActive;
   }
   
   public final T workInTransaction() throws Exception
   {
      boolean transactionActive = Transactions.isTransactionActiveOrMarkedRollback();
      boolean begin = isNewTransactionRequired(transactionActive);
      UserTransaction userTransaction = begin ? Transactions.getUserTransaction() : null;
      
      if (begin) 
      {
         log.debug("beginning transaction");
         userTransaction.begin();
      }
      try
      {
         T result = work();
         if (begin) 
         {
            log.debug("committing transaction");
            userTransaction.commit();
         }
         return result;
      }
      catch (Exception e)
      {
         if (begin) 
         {
            log.debug("rolling back transaction");
            userTransaction.rollback();
         }
         throw e;
      }
   }
}
