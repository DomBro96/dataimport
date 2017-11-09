package cn.dombro.dataimport.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 该工具类 用来 解析 token 串的载荷中的 claim
 */
public class ClaimUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClaimUtil.class);
  //得到 请求头中 保存的 token
  private static Map<String,Object> getClaimByHeader(HttpServletRequest request,String headerName) {

      Map<String,Object> claim = new HashMap();
      //获取 token 串
      String token = request.getHeader(headerName);
      if (token != null){
          claim = WebTokenUtil.verifyJavaWebToken(token);
          return claim;
      } else {
          LOGGER.error(headerName + " 请求头中 并未发现 token 字符串 ");
          throw new RuntimeException(headerName + " 请求头中 并未发现 token 字符串 ");

      }
  }

  //获取 载荷中 claim 指定键的值
  public static Object getValueByHeader(HttpServletRequest request,String headerName,String claimKey){
       return getClaimByHeader(request,headerName).get(claimKey);
  }

  public static Object getValueByPara(HttpServletRequest request,String paraName,String claimKey){
      return getClaimByPara(request,paraName).get(claimKey);
  }

  private static Map<String,Object> getClaimByPara(HttpServletRequest request,String paraName){
       Map<String,Object> claim = new HashMap();
       String token = request.getParameter(paraName);
       if (token!=null){
           claim = WebTokenUtil.verifyJavaWebToken(token);
           return claim;
       }else {
           LOGGER.error(paraName + "  请求参数中并未发现 token 字符串 ");
           throw new RuntimeException(paraName + " 请求参数中并未发现 token 字符串 ");
       }
  }

}
