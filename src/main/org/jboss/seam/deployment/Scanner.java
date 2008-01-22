package org.jboss.seam.deployment;

import java.io.File;

/**
 * The Scanner is used to find resources to be processed by Seam
 * 
 * The processing is done by {@link DeploymentHandler}s
 * 
 * @author Pete Muir
 *
 */
public interface Scanner
{
   /**
    * Recursively scan directories
    * 
    * @param directories An array of the roots of the directory trees to scan
    */
   public void scanDirectories(File[] directories);
   
   /**
    * Scan for structures which contain any of the given resources in their root
    * 
    * @param resources The resources to scan for
    */
   public void scanResources(String[] resources);
   
   /**
    * Get the deployment strategy this scanner is used by
    */
   public DeploymentStrategy getDeploymentStrategy();
   
}
