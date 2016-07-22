/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;

/**
 *
 * @author ysa
 */
public class RewriteEntity
  implements Serializable
{
  private int typeRewrite;
  private String urlPrefix;
  private String urlRegex;
  private String urlRegexReplace;
  private int typeMove;
  private String urlMapping;
  private String urlMappingServlet;
  private boolean status;
  
  public int getTypeRewrite()
  {
    return this.typeRewrite;
  }
  
  public void setTypeRewrite(int typeRewrite)
  {
    this.typeRewrite = typeRewrite;
  }
  
  public String getUrlPrefix()
  {
    return this.urlPrefix;
  }
  
  public void setUrlPrefix(String urlPrefix)
  {
    this.urlPrefix = urlPrefix;
  }
  
  public String getUrlRegex()
  {
    return this.urlRegex;
  }
  
  public void setUrlRegex(String urlRegex)
  {
    this.urlRegex = urlRegex;
  }
  
  public String getUrlRegexReplace()
  {
    return this.urlRegexReplace;
  }
  
  public void setUrlRegexReplace(String urlRegexReplace)
  {
    this.urlRegexReplace = urlRegexReplace;
  }
  
  public int getTypeMove()
  {
    return this.typeMove;
  }
  
  public void setTypeMove(int typeMove)
  {
    this.typeMove = typeMove;
  }
  
  public String getUrlMapping()
  {
    return this.urlMapping;
  }
  
  public void setUrlMapping(String urlMapping)
  {
    this.urlMapping = urlMapping;
  }
  
  public String getUrlMappingServlet()
  {
    return this.urlMappingServlet;
  }
  
  public void setUrlMappingServlet(String urlMappingServlet)
  {
    this.urlMappingServlet = urlMappingServlet;
  }
  
  public boolean getStatus()
  {
    return this.status;
  }
  
  public void setStatus(boolean status)
  {
    this.status = status;
  }
}
