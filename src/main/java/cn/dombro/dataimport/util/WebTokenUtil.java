package cn.dombro.dataimport.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebTokenUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(WebTokenUtil.class);

    private static String secreatKey = "The.dataimport.TokenSecretKey";

    //保存在 内村中的 tokenList ，每次 新建 一个 token 串 ，就保存在 tokenList 中
    private static List<String> tokenList ;

    public static void addToTokenList(String token){
        tokenList.add(token);

    }
    public static void creatTokenList(){
        tokenList = new ArrayList();
    }

    public static List<String> getTokenList() {
        return tokenList;
    }

    //生成 secretKey
    private static Key getSecretKeyInstance(){

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] secretKeyByte = DatatypeConverter.parseBase64Binary(secreatKey);

        Key signingKey = new SecretKeySpec(secretKeyByte,signatureAlgorithm.getJcaName());

        return signingKey;
    }

    //根据 传入的参数  Map 作为载荷,和 header信息 以及 secretKey  一起 经过 HS25 加密算法  创建 Token 字符串,
    public static String createJavaWebToken(Map<String, Object> claims) {

        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256,getSecretKeyInstance()).compact();
    }


    public static Map<String, Object> verifyJavaWebToken(String token) {
        try {
            Map<String, Object> jwtClaims =
                    Jwts.parser().setSigningKey(getSecretKeyInstance()).parseClaimsJws(token).getBody();
            return jwtClaims;
        } catch (Exception e) {
            LOGGER.error("json web token 解析失败");
            return null;
        }
    }

}
