package egova.enums;

public enum ResultStatus {
    Error("0","错误"),
    Exception("1","异常"),
    ParameterError("1","参数错误");
    private final String description;
    private final String value;

    ResultStatus(String value, String description) {
        this.description = description;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
