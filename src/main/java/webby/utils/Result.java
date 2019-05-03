package webby.utils;


public class Result {
    private int code;
    private String msg;
    private Object data;

    Result() {
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this);
    }

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result success() {
        Result result = new Result();
        result.code = 200;
        result.msg = "成功";
        result.data = "";
        return result;
    }

    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.code = 200;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.code = 200;
        result.msg = "成功";
        result.data = data;
        return result;
    }

    public static Result error(int code, String msg) {
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        result.data = "";
        return result;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

