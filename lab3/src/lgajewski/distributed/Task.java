package lgajewski.distributed;

public enum Task {

    SUM("sum", "+"),
    SUBTRACT("subtract", "-");

    private final String taskName;
    private final String operator;

    Task(String taskName, String operator) {
        this.taskName = taskName;
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public String getTopicName() {
        return "t-" + taskName;
    }
}
