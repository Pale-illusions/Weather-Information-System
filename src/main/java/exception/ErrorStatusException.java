package exception;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */
public class ErrorStatusException extends Exception{
    private String code;

    public ErrorStatusException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "APIException{" +
                "code=" + code +
                ", message='" + getMessage() + '\'' +
                '}';
    }

    public static ErrorStatusException fromCode(String code) {
        switch (code) {
            case "204":
                return new ErrorStatusException(code, "请求成功，但你查询的地区暂时没有你需要的数据。");
            case "400":
                return new ErrorStatusException(code, "请求错误，可能包含错误的请求参数或缺少必选的请求参数。");
            case "401":
                return new ErrorStatusException(code, "认证失败，可能使用了错误的KEY、数字签名错误、KEY的类型错误。");
            case "402":
                return new ErrorStatusException(code, "超过访问次数或余额不足以支持继续访问服务。");
            case "403":
                return new ErrorStatusException(code, "无访问权限，可能是绑定的PackageName、BundleID、域名IP地址不一致。");
            case "404":
                return new ErrorStatusException(code, "查询的数据或地区不存在。");
            case "429":
                return new ErrorStatusException(code, "超过限定的QPM（每分钟访问次数），请参考QPM说明。");
            case "500":
                return new ErrorStatusException(code, "无响应或超时，接口服务异常。");
            default:
                return new ErrorStatusException(code, "未知错误");
        }
    }
}
